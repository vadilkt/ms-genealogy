package com.example.genealogie.util;

import com.example.genealogie.entities.Profile;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.security.core.parameters.P;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PDFGenerator {

    public static void generateProfilePDF(HttpServletResponse response, Profile profile) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        document.add(new Paragraph("Profil"));
        document.add(new Paragraph("Nom: " + profile.getNom()));
        document.add(new Paragraph("Prénom"+ profile.getPrenom()));
        document.add(new Paragraph("Téléphone: " + profile.getTelephone()));
        document.add(new Paragraph("Ville: " + profile.getVille()));
        document.add(new Paragraph("Pays: " + profile.getPays()));
        document.add(new Paragraph("Date de naissance: " + profile.getDateNaissance()));
        if(profile.getDateMort()==null){
            document.add(new Paragraph());
        }else {document.add(new Paragraph("Date de décès: " + profile.getDateNaissance()));}

        // Add academic background
        document.add(new Paragraph("Parcours Acadélique:"));
        profile.getParcoursAcademiques().forEach(academic -> {
            try {
                document.add(new Paragraph("Institution: " + academic.getInstitution()));
                document.add(new Paragraph("Diplôme: " + academic.getDiplome()));
                document.add(new Paragraph("Spécialité: " + academic.getSpecialite()));
                document.add(new Paragraph("Date de début: " + academic.getDateDebut()));
                document.add(new Paragraph("Date de fin: " + academic.getDateFin()));
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        });

        // Add professional experience
        document.add(new Paragraph("Expériences professionnelles:"));
        profile.getExperienceProfessionnelles().forEach(professional -> {
            try {
                document.add(new Paragraph("Entreprise: " + professional.getEntreprise()));
                document.add(new Paragraph("Poste: " + professional.getPoste()));
                document.add(new Paragraph("Date de début: " + professional.getDateDebut()));
                document.add(new Paragraph("Date de fin: " + professional.getDateFin()));
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        });

        document.close();
    }
}
