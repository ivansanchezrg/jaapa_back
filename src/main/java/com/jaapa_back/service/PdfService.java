package com.jaapa_back.service;

import com.jaapa_back.enums.TipoDocumentoEnum;
import com.jaapa_back.exception.custom.DocumentoException;
import com.jaapa_back.exception.custom.ValorActualIncorrectoException;
import com.jaapa_back.model.Direccion;
import com.jaapa_back.model.Documento;
import com.jaapa_back.model.Solicitud;
import com.jaapa_back.repository.DocumentoRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
public class PdfService {

    @Autowired
    private DocumentoRepository documentoRepository;

    @Value("${app.storage.path:documentos/pdfs}")  // valor por defecto si no se especifica
    private String storagePath;

    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(storagePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            log.error("No se pudo crear el directorio para almacenar los PDFs", e);
            throw new DocumentoException("Error al inicializar el servicio de documentos", e);
        }
    }

    public byte[] generatePdf(Solicitud solicitud) {
        // Validación inicial
        if (solicitud == null) {
            throw new ValorActualIncorrectoException("La solicitud no puede ser nula");
        }

        if (solicitud.getUsuario() == null) {
            throw new ValorActualIncorrectoException("La solicitud debe tener un usuario");
        }

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                PDFont fontRegular = PDType1Font.HELVETICA;
                PDFont fontBold = PDType1Font.HELVETICA_BOLD;
                float fontSize = 12;

                addHeader(content, solicitud, fontRegular, fontBold, fontSize);
                addMainContent(content, solicitud, fontRegular, fontBold, fontSize);
                addContactInfo(content, solicitud, fontRegular, fontBold, fontSize);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Error generando PDF para la solicitud: {}",
                    solicitud.getNumeroSolicitud(), e);
            throw new DocumentoException("Error al generar el documento PDF", e);
        }
    }

    private void addHeader(PDPageContentStream content, Solicitud solicitud,
                           PDFont fontRegular, PDFont fontBold, float fontSize) {
        try {
            // Fecha actual
            LocalDate fecha = LocalDate.now();
            String fechaFormateada = "CUENCA, " + fecha.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy",
                    new Locale("es", "EC")));

            float pageWidth = PDRectangle.A4.getWidth();
            float textWidth = fontRegular.getStringWidth(fechaFormateada) / 1000 * fontSize;

            // Bajamos la fecha de 800 a 750
            content.beginText();
            content.setFont(fontRegular, fontSize);
            content.newLineAtOffset(pageWidth - textWidth - 50, 750);
            content.showText(fechaFormateada);
            content.endText();

            content.beginText();
            content.setFont(fontRegular, fontSize);
            content.newLineAtOffset(50, 670);
            content.showText("N° Solicitud: ");
            content.setFont(fontBold, fontSize);
            content.showText(Optional.ofNullable(solicitud.getNumeroSolicitud())
                    .orElse("Sin número"));
            content.endText();
        } catch (IOException e) {
            log.error("Error al generar encabezado del PDF para la solicitud: {}",
                    solicitud.getNumeroSolicitud(), e);
            throw new DocumentoException("Error al generar encabezado del documento", e);
        }
    }

    private void addMainContent(PDPageContentStream content, Solicitud solicitud,
                                PDFont fontRegular, PDFont fontBold, float fontSize) {
        try {
            float pageWidth = PDRectangle.A4.getWidth();

            String[] headerLines = {
                    "Señor (a)",
                    "DIRECTOR(A) DE LA JUNTA ADMINISTRADORA DE AGUA POTABLE",
                    "Y ALCANTARILLADO DE LA COMUNIDAD SAN JOSÉ DE RARANGA",
                    "Ciudad"
            };

            float y = 620;
            for (String line : headerLines) {
                content.beginText();
                content.setFont(fontRegular, fontSize);
                content.newLineAtOffset(50, y);
                content.showText(line);
                content.endText();
                y -= 15;
            }

            y -= 15; // Agregamos 20 unidades más de espacio después de "De mi consideración"

            // Agregar "De mi consideración"
            y -= 15; // Espacio adicional antes de la nueva línea
            content.beginText();
            content.setFont(fontRegular, fontSize);
            content.newLineAtOffset(50, y);
            content.showText("De mi consideración:");
            content.endText();

            // Extraer datos con validación
            String nombre = Optional.ofNullable(solicitud.getUsuario().getNombre()).orElse("").toUpperCase();
            String apellido = Optional.ofNullable(solicitud.getUsuario().getApellido()).orElse("").toUpperCase();
            String cedula = Optional.ofNullable(solicitud.getUsuario().getCedula()).orElse("");
            String tipoServicio = Optional.ofNullable(solicitud.getTipoSolicitud())
                    .map(ts -> ts.getNombre())
                    .orElse("NO ESPECIFICADO");
            String direccion = Optional.ofNullable(solicitud.getDireccion())
                    .map(this::formatearDireccion)
                    .orElse("NO ESPECIFICADA");

            String mainText = String.format("Yo, %s %s, con documento de identificación No. %s, solicito de la manera más comedida " +
                            "la instalación del servicio de %s para mi propiedad ubicada en %s.",
                    nombre, apellido, cedula, tipoServicio, direccion);

            addWrappedText(content, mainText, 50, y - 50, pageWidth - 100, fontSize, fontRegular);

            String declarationText = "Por medio de la presente, declaro que la información proporcionada es verídica " +
                    "y me comprometo a cumplir con las normativas, regulaciones y obligaciones establecidas por la " +
                    "Junta Administradora para el servicio solicitado.";

            addWrappedText(content, declarationText, 50, y - 140, pageWidth - 100, fontSize, fontRegular);

            y = y - 120; // La misma posición que usamos para el texto de declaración

            // Sección de firma
            float centerX = pageWidth / 2;
            float firmaY = y - 140; // Ajustamos este valor para bajar la firma

            // Atentamente
            content.beginText();
            content.setFont(fontRegular, fontSize);
            content.newLineAtOffset(centerX - (getTextWidth("Atentamente", fontRegular, fontSize) / 2), firmaY);
            content.showText("Atentamente");
            content.endText();

            // Línea de firma - aumentamos el valor que se resta
            firmaY -= 90; // Cambiamos de 40 a 60
            float lineWidth = 200;
            content.moveTo(centerX - (lineWidth/2), firmaY);
            content.lineTo(centerX + (lineWidth/2), firmaY);
            content.stroke();

            // Texto bajo la firma
            firmaY -= 20;
            String textoFirma = "Firma del Solicitante";
            content.beginText();
            content.setFont(fontRegular, fontSize);
            content.newLineAtOffset(centerX - (getTextWidth(textoFirma, fontRegular, fontSize) / 2), firmaY);
            content.showText(textoFirma);
            content.endText();

        } catch (IOException e) {
            log.error("Error al generar contenido principal del PDF para la solicitud: {}",
                    solicitud.getNumeroSolicitud(), e);
            throw new DocumentoException("Error al generar contenido del documento", e);
        }
    }

    private String formatearDireccion(Direccion direccion) {
        if (direccion == null) return "NO ESPECIFICADA";

        StringBuilder sb = new StringBuilder();
        if (direccion.getCallePrincipal() != null) {
            sb.append("Calle Principal: ").append(direccion.getCallePrincipal());
        }
        if (direccion.getCalleSecundaria() != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("Calle Secundaria: ").append(direccion.getCalleSecundaria());
        }
        if (direccion.getBarrio() != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("Barrio: ").append(direccion.getBarrio());
        }
        if (direccion.getReferencia() != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("Referencia: ").append(direccion.getReferencia());
        }
        return sb.length() > 0 ? sb.toString() : "NO ESPECIFICADA";
    }

    private float getTextWidth(String text, PDFont font, float fontSize) throws IOException {
        return font.getStringWidth(text) * fontSize / 1000;
    }

    private void addWrappedText(PDPageContentStream content, String text, float x, float y,
                                float width, float fontSize, PDFont font) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }

        try {
            String[] words = text.split(" ");
            List<String> lines = new ArrayList<>();
            StringBuilder currentLine = new StringBuilder();

            // Primera pasada: dividir el texto en líneas
            for (String word : words) {
                if (word == null) continue;

                float lineWidth = getTextWidth(currentLine + word + " ", font, fontSize);
                if (lineWidth > width) {
                    lines.add(currentLine.toString().trim());
                    currentLine = new StringBuilder(word + " ");
                } else {
                    currentLine.append(word).append(" ");
                }
            }
            // Agregar la última línea
            if (currentLine.length() > 0) {
                lines.add(currentLine.toString().trim());
            }

            float lineY = y;
            // Segunda pasada: renderizar las líneas con justificación
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                boolean isLastLine = i == lines.size() - 1;

                if (!isLastLine) {
                    // Justificar todas las líneas excepto la última
                    String[] lineWords = line.split(" ");
                    float textWidth = getTextWidth(line, font, fontSize);
                    float remainingSpace = width - textWidth;

                    if (lineWords.length > 1) {  // Solo justificar si hay más de una palabra
                        float extraSpacePerGap = remainingSpace / (lineWords.length - 1);

                        content.beginText();
                        content.setFont(font, fontSize);
                        content.newLineAtOffset(x, lineY);

                        // Dibujar cada palabra con el espaciado calculado
                        float currentX = 0;
                        for (int j = 0; j < lineWords.length; j++) {
                            content.showText(lineWords[j]);
                            if (j < lineWords.length - 1) {
                                float wordWidth = getTextWidth(lineWords[j], font, fontSize);
                                currentX += wordWidth + getTextWidth(" ", font, fontSize) + extraSpacePerGap;
                                content.newLineAtOffset(wordWidth + getTextWidth(" ", font, fontSize) + extraSpacePerGap, 0);
                            }
                        }
                        content.endText();
                    } else {
                        // Si solo hay una palabra en la línea, centrarla
                        content.beginText();
                        content.setFont(font, fontSize);
                        content.newLineAtOffset(x, lineY);
                        content.showText(line);
                        content.endText();
                    }
                } else {
                    // La última línea se alinea a la izquierda
                    content.beginText();
                    content.setFont(font, fontSize);
                    content.newLineAtOffset(x, lineY);
                    content.showText(line);
                    content.endText();
                }

                lineY -= fontSize * 1.5;
            }
        } catch (IOException e) {
            log.error("Error al formatear texto para PDF", e);
            throw new DocumentoException("Error al formatear texto del documento", e);
        }
    }

    private void addContactInfo(PDPageContentStream content, Solicitud solicitud,
                                PDFont fontRegular, PDFont fontBold, float fontSize) {
        try {
            float y = 120;  // Bajamos de 280 a 200 para que esté debajo de la firma

            content.beginText();
            content.setFont(fontBold, fontSize);
            content.newLineAtOffset(50, y);
            content.showText("Datos del solicitante:");
            content.endText();

            y -= 20;

            if (solicitud.getUsuario() != null) {
                String telefono = solicitud.getUsuario().getTelefono();
                if (telefono != null && !telefono.trim().isEmpty()) {
                    content.beginText();
                    content.setFont(fontRegular, fontSize);
                    content.newLineAtOffset(50, y);
                    content.showText("Teléfono: " + telefono);
                    content.endText();
                    y -= 15;
                }

                String celular = solicitud.getUsuario().getCelular();
                if (celular != null && !celular.trim().isEmpty()) {
                    content.beginText();
                    content.setFont(fontRegular, fontSize);
                    content.newLineAtOffset(50, y);
                    content.showText("Celular: " + celular);
                    content.endText();
                    y -= 15;
                }

                String correo = solicitud.getUsuario().getCorreo();
                if (correo != null && !correo.trim().isEmpty()) {
                    content.beginText();
                    content.setFont(fontRegular, fontSize);
                    content.newLineAtOffset(50, y);
                    content.showText("Correo: " + correo);
                    content.endText();
                }
            }
        } catch (IOException e) {
            log.error("Error al agregar información de contacto al PDF para la solicitud: {}",
                    solicitud.getNumeroSolicitud(), e);
            throw new DocumentoException("Error al agregar información de contacto al documento", e);
        }
    }

    public Documento savePdfToFile(Solicitud solicitud) {
        try {
            // Generar el PDF
            byte[] pdfContent = generatePdf(solicitud);

            // Crear nombre único para el archivo
            String nombreArchivo = String.format("solicitud_%s.pdf",
                    solicitud.getNumeroSolicitud());

            // Asegurar que el directorio existe
            Path dirPath = Paths.get(storagePath);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // Guardar el archivo
            Path filePath = dirPath.resolve(nombreArchivo);
            Files.write(filePath, pdfContent);

            // Crear y guardar el registro en la base de datos
            Documento documento = new Documento();
            documento.setNombre(nombreArchivo);
            documento.setRuta(filePath.toString());
            documento.setTipoDocumentoEnum(TipoDocumentoEnum.SOLICITUD);
            documento.setFechaCreacion(LocalDateTime.now());
            documento.setSolicitud(solicitud);
            documento.setTamanio((long) pdfContent.length);

            return documentoRepository.save(documento);

        } catch (DocumentoException e) {
            // Propagar las excepciones de documento que ya están correctamente formateadas
            log.error("Error al guardar el documento para la solicitud: {}",
                    solicitud.getNumeroSolicitud(), e);
            throw e;
        } catch (IOException e) {
            log.error("Error de E/S al guardar el PDF para la solicitud: {}",
                    solicitud.getNumeroSolicitud(), e);
            throw new DocumentoException("Error al guardar el documento en el sistema", e);
        } catch (Exception e) {
            log.error("Error inesperado al guardar el PDF para la solicitud: {}",
                    solicitud.getNumeroSolicitud(), e);
            throw new DocumentoException("Error inesperado al procesar el documento", e);
        }
    }
}
