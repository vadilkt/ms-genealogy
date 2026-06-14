package com.example.genealogie.service.impl;

import com.example.genealogie.model.DatePrecision;
import com.example.genealogie.model.DateQualifier;
import com.example.genealogie.model.Gender;
import com.example.genealogie.model.Marriage;
import com.example.genealogie.model.Place;
import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;
import com.example.genealogie.repository.MarriageRepository;
import com.example.genealogie.repository.ProfileRepository;
import com.example.genealogie.service.GedcomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GedcomServiceImpl implements GedcomService {

    private static final String[] MONTHS =
            {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

    private final ProfileRepository profileRepository;
    private final MarriageRepository marriageRepository;

    // ─── Export ─────────────────────────────────────────────────────────────

    /** Famille GEDCOM : un couple (mari/épouse) et ses enfants. */
    private static class Fam {
        int id;
        final Long husband;
        final Long wife;
        final Set<Long> children = new LinkedHashSet<>();
        ZonedDateTime marriageDate;

        Fam(Long husband, Long wife) {
            this.husband = husband;
            this.wife = wife;
        }
    }

    private static String key(Long a, Long b) {
        return (a == null ? "" : a) + "_" + (b == null ? "" : b);
    }

    @Override
    @Transactional(readOnly = true)
    public String exportGedcom() {
        List<Profile> profiles = profileRepository.findAll();
        List<Marriage> marriages = marriageRepository.findAll();

        LinkedHashMap<String, Fam> fams = new LinkedHashMap<>();
        for (Marriage m : marriages) {
            Long h = m.getHusband().getId();
            Long w = m.getWife().getId();
            fams.computeIfAbsent(key(h, w), k -> new Fam(h, w)).marriageDate = m.getMarriageDate();
        }
        for (Profile p : profiles) {
            Long fa = p.getFather() != null ? p.getFather().getId() : null;
            Long mo = p.getMother() != null ? p.getMother().getId() : null;
            if (fa == null && mo == null) continue;
            fams.computeIfAbsent(key(fa, mo), k -> new Fam(fa, mo)).children.add(p.getId());
        }
        int fi = 1;
        for (Fam f : fams.values()) f.id = fi++;

        Map<Long, List<Integer>> spouseFams = new HashMap<>();
        Map<Long, Integer> childFam = new HashMap<>();
        for (Fam f : fams.values()) {
            if (f.husband != null) spouseFams.computeIfAbsent(f.husband, k -> new ArrayList<>()).add(f.id);
            if (f.wife != null) spouseFams.computeIfAbsent(f.wife, k -> new ArrayList<>()).add(f.id);
            for (Long c : f.children) childFam.put(c, f.id);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("0 HEAD\n");
        sb.append("1 SOUR ms-genealogie\n");
        sb.append("1 GEDC\n2 VERS 5.5.1\n2 FORM LINEAGE-LINKED\n");
        sb.append("1 CHAR UTF-8\n");

        for (Profile p : profiles) {
            sb.append("0 @I").append(p.getId()).append("@ INDI\n");
            String given = p.getFirstName() != null ? p.getFirstName() : "";
            String surname = p.getLastName() != null ? p.getLastName() : "";
            sb.append("1 NAME ").append(given).append(" /").append(surname).append("/\n");
            if (p.getGender() != null) {
                sb.append("1 SEX ").append(p.getGender() == Gender.MALE ? "M" : "F").append("\n");
            }
            appendEvent(sb, "BIRT",
                    gedcomDate(p.getDateOfBirth(), p.getBirthDateQualifier(), p.getBirthDatePrecision()),
                    placeLabel(p.getBirthPlace()));
            appendEvent(sb, "DEAT",
                    gedcomDate(p.getDateOfDeath(), p.getDeathDateQualifier(), p.getDeathDatePrecision()),
                    placeLabel(p.getDeathPlace()));
            Integer fc = childFam.get(p.getId());
            if (fc != null) sb.append("1 FAMC @F").append(fc).append("@\n");
            for (Integer fs : spouseFams.getOrDefault(p.getId(), List.of())) {
                sb.append("1 FAMS @F").append(fs).append("@\n");
            }
        }

        for (Fam f : fams.values()) {
            sb.append("0 @F").append(f.id).append("@ FAM\n");
            if (f.husband != null) sb.append("1 HUSB @I").append(f.husband).append("@\n");
            if (f.wife != null) sb.append("1 WIFE @I").append(f.wife).append("@\n");
            for (Long c : f.children) sb.append("1 CHIL @I").append(c).append("@\n");
            if (f.marriageDate != null) {
                sb.append("1 MARR\n2 DATE ")
                        .append(gedcomDate(f.marriageDate, DateQualifier.EXACT, DatePrecision.DAY))
                        .append("\n");
            }
        }

        sb.append("0 TRLR\n");
        return sb.toString();
    }

    private void appendEvent(StringBuilder sb, String tag, String date, String place) {
        if (date == null && place == null) return;
        sb.append("1 ").append(tag).append("\n");
        if (date != null) sb.append("2 DATE ").append(date).append("\n");
        if (place != null) sb.append("2 PLAC ").append(place).append("\n");
    }

    private String placeLabel(Place p) {
        if (p == null) return null;
        String city = p.getCity() != null ? p.getCity() : "";
        String country = p.getCountry() != null ? p.getCountry() : "";
        String label = (city + ", " + country).replaceAll("^, |, $", "").trim();
        return label.isEmpty() ? null : label;
    }

    private String gedcomDate(ZonedDateTime dt, DateQualifier qualifier, DatePrecision precision) {
        if (dt == null) return null;
        LocalDate d = dt.toLocalDate();
        DatePrecision p = precision == null ? DatePrecision.DAY : precision;
        String body = switch (p) {
            case YEAR -> String.valueOf(d.getYear());
            case MONTH -> MONTHS[d.getMonthValue() - 1] + " " + d.getYear();
            default -> d.getDayOfMonth() + " " + MONTHS[d.getMonthValue() - 1] + " " + d.getYear();
        };
        String prefix = switch (qualifier == null ? DateQualifier.EXACT : qualifier) {
            case ABOUT -> "ABT ";
            case BEFORE -> "BEF ";
            case AFTER -> "AFT ";
            default -> "";
        };
        return prefix + body;
    }

    // ─── Import ─────────────────────────────────────────────────────────────

    /** Données brutes d'un individu lues dans le fichier. */
    private static class Indi {
        String given = "";
        String surname = "";
        Gender gender;
        ZonedDateTime birth;
        DateQualifier birthQ = DateQualifier.EXACT;
        DatePrecision birthP = DatePrecision.DAY;
        ZonedDateTime death;
        DateQualifier deathQ = DateQualifier.EXACT;
        DatePrecision deathP = DatePrecision.DAY;
    }

    private static class FamRec {
        String husband;
        String wife;
        final List<String> children = new ArrayList<>();
        ZonedDateTime marriageDate;
    }

    @Override
    @Transactional
    public int importGedcom(String gedcom, User currentUser) {
        Map<String, Indi> indis = new LinkedHashMap<>();
        Map<String, FamRec> fams = new LinkedHashMap<>();

        String currentXref = null;
        String currentType = null; // INDI | FAM
        String currentEvent = null; // BIRT | DEAT | MARR
        Indi indi = null;
        FamRec fam = null;

        for (String raw : gedcom.split("\\r?\\n")) {
            String line = raw.trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split(" ", 2);
            int level;
            try {
                level = Integer.parseInt(parts[0]);
            } catch (NumberFormatException e) {
                continue;
            }
            String rest = parts.length > 1 ? parts[1] : "";

            if (level == 0) {
                currentEvent = null;
                if (rest.matches("@\\w+@ INDI")) {
                    currentXref = rest.substring(0, rest.indexOf(' '));
                    currentType = "INDI";
                    indi = new Indi();
                    indis.put(currentXref, indi);
                    fam = null;
                } else if (rest.matches("@\\w+@ FAM")) {
                    currentXref = rest.substring(0, rest.indexOf(' '));
                    currentType = "FAM";
                    fam = new FamRec();
                    fams.put(currentXref, fam);
                    indi = null;
                } else {
                    currentType = null;
                    indi = null;
                    fam = null;
                }
                continue;
            }

            String[] tagParts = rest.split(" ", 2);
            String tag = tagParts[0];
            String value = tagParts.length > 1 ? tagParts[1].trim() : "";

            if ("INDI".equals(currentType) && indi != null) {
                if (level == 1) {
                    currentEvent = null;
                    switch (tag) {
                        case "NAME" -> parseName(value, indi);
                        case "SEX" -> indi.gender = "M".equalsIgnoreCase(value) ? Gender.MALE
                                : "F".equalsIgnoreCase(value) ? Gender.FEMALE : null;
                        case "BIRT" -> currentEvent = "BIRT";
                        case "DEAT" -> currentEvent = "DEAT";
                        default -> { }
                    }
                } else if (level == 2 && "DATE".equals(tag)) {
                    ParsedDate pd = parseGedcomDate(value);
                    if (pd != null && "BIRT".equals(currentEvent)) {
                        indi.birth = pd.date;
                        indi.birthQ = pd.qualifier;
                        indi.birthP = pd.precision;
                    } else if (pd != null && "DEAT".equals(currentEvent)) {
                        indi.death = pd.date;
                        indi.deathQ = pd.qualifier;
                        indi.deathP = pd.precision;
                    }
                }
            } else if ("FAM".equals(currentType) && fam != null) {
                if (level == 1) {
                    currentEvent = null;
                    switch (tag) {
                        case "HUSB" -> fam.husband = value;
                        case "WIFE" -> fam.wife = value;
                        case "CHIL" -> fam.children.add(value);
                        case "MARR" -> currentEvent = "MARR";
                        default -> { }
                    }
                } else if (level == 2 && "DATE".equals(tag) && "MARR".equals(currentEvent)) {
                    ParsedDate pd = parseGedcomDate(value);
                    if (pd != null) fam.marriageDate = pd.date;
                }
            }
        }

        // 1) Créer les profils
        Map<String, Profile> saved = new HashMap<>();
        for (Map.Entry<String, Indi> e : indis.entrySet()) {
            Indi d = e.getValue();
            Profile p = new Profile();
            p.setFirstName(d.given.isEmpty() ? null : d.given);
            p.setLastName(d.surname.isEmpty() ? null : d.surname);
            p.setGender(d.gender);
            p.setDateOfBirth(d.birth);
            p.setBirthDateQualifier(d.birthQ);
            p.setBirthDatePrecision(d.birthP);
            p.setDateOfDeath(d.death);
            p.setDeathDateQualifier(d.deathQ);
            p.setDeathDatePrecision(d.deathP);
            saved.put(e.getKey(), profileRepository.save(p));
        }

        // 2) Appliquer les liens familiaux
        for (FamRec f : fams.values()) {
            Profile husband = f.husband != null ? saved.get(f.husband) : null;
            Profile wife = f.wife != null ? saved.get(f.wife) : null;
            for (String childRef : f.children) {
                Profile child = saved.get(childRef);
                if (child == null) continue;
                if (husband != null) child.setFather(husband);
                if (wife != null) child.setMother(wife);
                profileRepository.save(child);
            }
            // Mariage best-effort : seulement si couple complet et épouse libre.
            if (husband != null && wife != null && husband.getGender() == Gender.MALE
                    && wife.getGender() == Gender.FEMALE
                    && !marriageRepository.existsByWife_IdAndEndDateIsNull(wife.getId())) {
                Marriage m = new Marriage();
                m.setHusband(husband);
                m.setWife(wife);
                m.setMarriageDate(f.marriageDate);
                marriageRepository.save(m);
            }
        }

        return saved.size();
    }

    private void parseName(String value, Indi indi) {
        // Format GEDCOM : "Prénom /Nom/"
        int slash = value.indexOf('/');
        if (slash >= 0) {
            indi.given = value.substring(0, slash).trim();
            int end = value.indexOf('/', slash + 1);
            indi.surname = end > slash ? value.substring(slash + 1, end).trim() : value.substring(slash + 1).trim();
        } else {
            indi.given = value.trim();
        }
    }

    private record ParsedDate(ZonedDateTime date, DateQualifier qualifier, DatePrecision precision) { }

    private ParsedDate parseGedcomDate(String value) {
        if (value == null || value.isBlank()) return null;
        String s = value.trim().toUpperCase();
        DateQualifier qualifier = DateQualifier.EXACT;
        if (s.startsWith("ABT ")) { qualifier = DateQualifier.ABOUT; s = s.substring(4); }
        else if (s.startsWith("BEF ")) { qualifier = DateQualifier.BEFORE; s = s.substring(4); }
        else if (s.startsWith("AFT ")) { qualifier = DateQualifier.AFTER; s = s.substring(4); }
        s = s.trim();

        String[] tok = s.split("\\s+");
        try {
            int year;
            int month = 1;
            int day = 1;
            DatePrecision precision;
            if (tok.length >= 3) {
                day = Integer.parseInt(tok[0]);
                month = monthIndex(tok[1]);
                year = Integer.parseInt(tok[2]);
                precision = DatePrecision.DAY;
            } else if (tok.length == 2) {
                month = monthIndex(tok[0]);
                year = Integer.parseInt(tok[1]);
                precision = DatePrecision.MONTH;
            } else {
                year = Integer.parseInt(tok[0]);
                precision = DatePrecision.YEAR;
            }
            if (month < 1) { month = 1; }
            ZonedDateTime date = LocalDate.of(year, month, Math.max(day, 1)).atStartOfDay(ZoneOffset.UTC);
            return new ParsedDate(date, qualifier, precision);
        } catch (Exception e) {
            return null;
        }
    }

    private int monthIndex(String mon) {
        for (int i = 0; i < MONTHS.length; i++) {
            if (MONTHS[i].equalsIgnoreCase(mon)) return i + 1;
        }
        return -1;
    }
}
