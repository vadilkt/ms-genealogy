package com.example.genealogie.controller;

import com.example.genealogie.model.User;
import com.example.genealogie.model.UserRole;
import com.example.genealogie.service.GedcomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gedcom")
public class GedcomController {

    private final GedcomService gedcomService;

    /** Exporte l'arbre complet au format GEDCOM (fichier téléchargeable). */
    @GetMapping("/export")
    public ResponseEntity<byte[]> export() {
        byte[] body = gedcomService.exportGedcom().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"ms-genealogie.ged\"")
                .contentType(MediaType.valueOf("application/x-gedcom"))
                .body(body);
    }

    /** Importe un fichier GEDCOM (corps texte brut). Réservé aux administrateurs. */
    @PostMapping(value = "/import", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Map<String, Object>> importGedcom(@RequestBody String gedcom,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(403).build();
        }
        int created = gedcomService.importGedcom(gedcom, currentUser);
        return ResponseEntity.ok(Map.of("created", created));
    }
}
