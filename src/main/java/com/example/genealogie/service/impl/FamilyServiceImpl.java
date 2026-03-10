package com.example.genealogie.service.impl;

import com.example.genealogie.dto.*;
import com.example.genealogie.mapper.ProfileMapper;
import com.example.genealogie.model.Gender;
import com.example.genealogie.model.Marriage;
import com.example.genealogie.model.Profile;
import com.example.genealogie.model.User;
import com.example.genealogie.repository.MarriageRepository;
import com.example.genealogie.repository.ProfileRepository;
import com.example.genealogie.service.FamilyService;
import com.example.genealogie.service.ProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FamilyServiceImpl implements FamilyService {

    private final ProfileRepository profileRepository;
    private final MarriageRepository marriageRepository;
    private final ProfileService profileService;
    private final ProfileMapper profileMapper;

    @Override
    @Transactional
    public Profile setFather(Long profileId, Long fatherProfileId, User currentUser) {
        if (profileId.equals(fatherProfileId)) {
            throw new IllegalArgumentException("Un profil ne peut pas être son propre père.");
        }
        Profile child = profileService.getProfileById(profileId);
        validateCanEdit(child, currentUser);
        Profile father = profileService.getProfileById(fatherProfileId);
        if (father.getGender() != Gender.MALE) {
            throw new IllegalArgumentException("Le père doit être de sexe masculin.");
        }
        if (isDescendant(profileId, fatherProfileId)) {
            throw new IllegalArgumentException("Cycle détecté : ce profil est déjà un descendant du père proposé.");
        }
        child.setFather(father);
        return profileRepository.save(child);
    }

    @Override
    @Transactional
    public Profile setMother(Long profileId, Long motherProfileId, User currentUser) {
        if (profileId.equals(motherProfileId)) {
            throw new IllegalArgumentException("Un profil ne peut pas être sa propre mère.");
        }
        Profile child = profileService.getProfileById(profileId);
        validateCanEdit(child, currentUser);
        Profile mother = profileService.getProfileById(motherProfileId);
        if (mother.getGender() != Gender.FEMALE) {
            throw new IllegalArgumentException("La mère doit être de sexe féminin.");
        }
        if (isDescendant(profileId, motherProfileId)) {
            throw new IllegalArgumentException("Cycle détecté : ce profil est déjà un descendant de la mère proposée.");
        }
        child.setMother(mother);
        return profileRepository.save(child);
    }

    @Override
    @Transactional
    public void removeFather(Long profileId, User currentUser) {
        Profile profile = profileService.getProfileById(profileId);
        validateCanEdit(profile, currentUser);
        profile.setFather(null);
        profileRepository.save(profile);
    }

    @Override
    @Transactional
    public void removeMother(Long profileId, User currentUser) {
        Profile profile = profileService.getProfileById(profileId);
        validateCanEdit(profile, currentUser);
        profile.setMother(null);
        profileRepository.save(profile);
    }

    @Override
    @Transactional
    public MarriageResponseDto addMarriage(Long profileId, CreateMarriageRequestDto request, User currentUser) {
        Profile profile = profileService.getProfileById(profileId);
        validateCanEdit(profile, currentUser);
        Profile spouse = profileService.getProfileById(request.getSpouseProfileId());

        Profile husband;
        Profile wife;
        if (profile.getGender() == Gender.MALE && spouse.getGender() == Gender.FEMALE) {
            husband = profile;
            wife = spouse;
        } else if (profile.getGender() == Gender.FEMALE && spouse.getGender() == Gender.MALE) {
            husband = spouse;
            wife = profile;
        } else {
            throw new IllegalArgumentException("Un mariage doit associer un homme et une femme.");
        }

        if (marriageRepository.existsByWife_Id(wife.getId())) {
            throw new IllegalArgumentException(
                    "Cette personne est déjà mariée. Une femme ne peut avoir qu'un seul mari.");
        }

        Marriage marriage = new Marriage();
        marriage.setHusband(husband);
        marriage.setWife(wife);
        marriage.setMarriageDate(request.getMarriageDate());
        marriage.setEndDate(request.getEndDate());
        husband.getMarriagesAsHusband().add(marriage);
        wife.setMarriageAsWife(marriage);
        marriage = marriageRepository.save(marriage);
        return toMarriageResponseDto(marriage);
    }

    @Override
    @Transactional
    public void removeMarriage(Long marriageId, User currentUser) {
        Marriage marriage = marriageRepository.findById(marriageId)
                .orElseThrow(() -> new EntityNotFoundException("Mariage non trouvé : " + marriageId));
        boolean canEditHusband = profileService.isOwnerOrAdmin(marriage.getHusband(), currentUser);
        boolean canEditWife = profileService.isOwnerOrAdmin(marriage.getWife(), currentUser);
        if (!canEditHusband && !canEditWife) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Vous n'êtes pas autorisé à supprimer ce mariage.");
        }
        marriage.getHusband().getMarriagesAsHusband().remove(marriage);
        marriage.getWife().setMarriageAsWife(null);
        marriageRepository.delete(marriage);
    }

    @Override
    @Transactional(readOnly = true)
    public FamilyResponseDto getParents(Long profileId, User currentUser) {
        Profile profile = profileService.getProfileById(profileId);
        return FamilyResponseDto.builder()
                .father(profileMapper.toDtoSummary(profile.getFather()))
                .mother(profileMapper.toDtoSummary(profile.getMother()))
                .children(List.of())
                .siblings(List.of())
                .spouses(List.of())
                .marriages(List.of())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileResponseDto> getChildren(Long profileId, User currentUser) {
        profileService.getProfileById(profileId);
        List<Profile> asFather = profileRepository.findByFather_Id(profileId);
        List<Profile> asMother = profileRepository.findByMother_Id(profileId);
        return Stream.concat(asFather.stream(), asMother.stream())
                .distinct()
                .map(profileMapper::toDtoSummary)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileResponseDto> getSpouses(Long profileId, User currentUser) {
        Profile profile = profileService.getProfileById(profileId);
        List<ProfileResponseDto> result = new ArrayList<>();
        if (profile.getGender() == Gender.MALE) {
            List<Marriage> marriages = marriageRepository.findByHusband_Id(profileId);
            for (Marriage m : marriages) {
                result.add(profileMapper.toDtoSummary(m.getWife()));
            }
        } else {
            marriageRepository.findByWife_Id(profileId)
                    .map(m -> profileMapper.toDtoSummary(m.getHusband()))
                    .ifPresent(result::add);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileResponseDto> getSiblings(Long profileId, User currentUser) {
        Profile profile = profileService.getProfileById(profileId);
        Long fatherId = profile.getFather() != null ? profile.getFather().getId() : null;
        Long motherId = profile.getMother() != null ? profile.getMother().getId() : null;
        if (fatherId == null && motherId == null) {
            return List.of();
        }
        Stream<Profile> fromFather = fatherId != null ? profileRepository.findByFather_Id(fatherId).stream()
                : Stream.empty();
        Stream<Profile> fromMother = motherId != null ? profileRepository.findByMother_Id(motherId).stream()
                : Stream.empty();
        return Stream.concat(fromFather, fromMother)
                .filter(p -> !Objects.equals(p.getId(), profileId))
                .collect(Collectors.toMap(Profile::getId, p -> p, (a, b) -> a))
                .values().stream()
                .map(profileMapper::toDtoSummary)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FamilyResponseDto getFamily(Long profileId, User currentUser) {
        Profile profile = profileService.getProfileById(profileId);
        List<ProfileResponseDto> children = getChildren(profileId, currentUser);
        List<ProfileResponseDto> siblings = getSiblings(profileId, currentUser);
        List<ProfileResponseDto> spouses = getSpouses(profileId, currentUser);
        List<MarriageResponseDto> marriages = new ArrayList<>();
        if (profile.getGender() == Gender.MALE) {
            for (Marriage m : marriageRepository.findByHusband_Id(profileId)) {
                marriages.add(toMarriageResponseDto(m));
            }
        } else {
            marriageRepository.findByWife_Id(profileId).ifPresent(m -> marriages.add(toMarriageResponseDto(m)));
        }

        return FamilyResponseDto.builder()
                .father(profileMapper.toDtoSummary(profile.getFather()))
                .mother(profileMapper.toDtoSummary(profile.getMother()))
                .children(children)
                .siblings(siblings)
                .spouses(spouses)
                .marriages(marriages)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AncestorNodeDto getAncestors(Long profileId, int depth, User currentUser) {
        profileService.getProfileById(profileId);
        Profile profile = profileRepository.findById(profileId).orElseThrow(
                () -> new EntityNotFoundException("Profil non trouvé : " + profileId));
        int effectiveDepth = depth <= 0 ? 5 : Math.min(depth, 10);
        return buildAncestorNode(profile, effectiveDepth);
    }

    /**
     * Construit un nœud d'ancêtres récursivement (père et mère, puis leurs parents,
     * etc.).
     * S'arrête quand remainingDepth <= 0 ou quand le profil est null.
     */
    private AncestorNodeDto buildAncestorNode(Profile profile, int remainingDepth) {
        if (profile == null || remainingDepth <= 0) {
            return null;
        }
        AncestorNodeDto fatherNode = buildAncestorNode(profile.getFather(), remainingDepth - 1);
        AncestorNodeDto motherNode = buildAncestorNode(profile.getMother(), remainingDepth - 1);
        return AncestorNodeDto.builder()
                .profile(profileMapper.toDtoSummary(profile))
                .father(fatherNode)
                .mother(motherNode)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DescendantNodeDto getDescendants(Long profileId, int depth, User currentUser) {
        profileService.getProfileById(profileId);
        Profile profile = profileRepository.findById(profileId).orElseThrow(
                () -> new EntityNotFoundException("Profil non trouvé : " + profileId));
        int effectiveDepth = depth <= 0 ? 5 : Math.min(depth, 10);
        return buildDescendantNode(profile, effectiveDepth);
    }

    /**
     * Construit un nœud de descendants récursivement (enfants, puis leurs enfants,
     * etc.).
     * S'arrête quand remainingDepth <= 0.
     */
    private DescendantNodeDto buildDescendantNode(Profile profile, int remainingDepth) {
        if (profile == null) {
            return null;
        }
        List<DescendantNodeDto> childNodes = List.of();
        if (remainingDepth > 0) {
            List<Profile> asFather = profileRepository.findByFather_Id(profile.getId());
            List<Profile> asMother = profileRepository.findByMother_Id(profile.getId());
            childNodes = Stream.concat(asFather.stream(), asMother.stream())
                    .collect(Collectors.toMap(Profile::getId, p -> p, (a, b) -> a))
                    .values().stream()
                    .map(child -> buildDescendantNode(child, remainingDepth - 1))
                    .filter(Objects::nonNull)
                    .toList();
        }
        return DescendantNodeDto.builder()
                .profile(profileMapper.toDtoSummary(profile))
                .children(childNodes)
                .build();
    }

    /**
     * Vérifie si {@code candidateId} est un descendant de {@code rootId}.
     * Utilisé pour détecter les cycles dans l'arbre généalogique.
     */
    private boolean isDescendant(Long rootId, Long candidateId) {
        Set<Long> visited = new HashSet<>();
        return hasDescendant(rootId, candidateId, visited);
    }

    private boolean hasDescendant(Long currentId, Long targetId, Set<Long> visited) {
        if (!visited.add(currentId)) return false;
        List<Profile> children = Stream.concat(
                profileRepository.findByFather_Id(currentId).stream(),
                profileRepository.findByMother_Id(currentId).stream()
        ).toList();
        for (Profile child : children) {
            if (child.getId().equals(targetId)) return true;
            if (hasDescendant(child.getId(), targetId, visited)) return true;
        }
        return false;
    }

    private void validateCanEdit(Profile profile, User currentUser) {
        if (!profileService.isOwnerOrAdmin(profile, currentUser)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Vous n'êtes pas autorisé à modifier ce profil.");
        }
    }

    private MarriageResponseDto toMarriageResponseDto(Marriage m) {
        MarriageResponseDto dto = new MarriageResponseDto();
        dto.setId(m.getId());
        dto.setHusbandId(m.getHusband().getId());
        dto.setWifeId(m.getWife().getId());
        dto.setHusband(profileMapper.toDtoSummary(m.getHusband()));
        dto.setWife(profileMapper.toDtoSummary(m.getWife()));
        dto.setMarriageDate(m.getMarriageDate());
        dto.setEndDate(m.getEndDate());
        return dto;
    }
}
