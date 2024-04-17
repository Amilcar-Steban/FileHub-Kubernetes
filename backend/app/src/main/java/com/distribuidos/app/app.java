package com.distribuidos.app;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import spark.Spark;
import com.google.gson.Gson;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class app {
    private static final String SAMBA_SERVER = "smb://sambadb/storage_samba";
    private static final String USERNAME = "user_samba";
    private static final String PASSWORD = "pass123.";
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        Spark.port(4567);
        Spark.externalStaticFileLocation("/public");

        // Configuración de CORS
        Spark.options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
            response.type("application/json");
        });

        // Endpoint para listar archivos
        Spark.get("/get_file_list", (request, response) -> {
            List<String> fileList = getFileListFromSamba();

            if (fileList == null) {
                response.status(500);
                return "No se pudo listar los archivos desde Samba.";
            }
            else{
                return gson.toJson(fileList);
            }
        });

        // Endpoint para subir archivos
        Spark.post("/upload", (request, response) -> {
            request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
            try {
                Part filePart = request.raw().getPart("file");
                String fileName = getFileName(filePart);
                InputStream fileStream = filePart.getInputStream();
                uploadToSamba(fileStream, fileName);
                return "Archivo cargado y almacenado en Samba exitosamente.";
            } catch (Exception e) {
                response.status(500);
                return "Error al cargar el archivo en Samba: " + e.getMessage();
            }
        });

        // Endpoint para borrar archivos
        Spark.delete("/delete/:fileName", (request, response) -> {
            String fileName = request.params(":fileName");
            boolean deleted = deleteFileFromSamba(fileName);

            if (deleted) {
                return "Archivo eliminado exitosamente.";
            } else {
                response.status(500);
                return "Error al eliminar el archivo desde Samba.";
            }
        });

        // Endpoint de salud
        Spark.get("/health", (req, res) -> {
            return "OK";
        });
    }

    private static boolean deleteFileFromSamba(String fileName) {
        try {
            String authString = USERNAME + ":" + PASSWORD + "@";
            String sambaUrl = "smb://" + authString + SAMBA_SERVER.substring(6) + "/" + fileName;
            SmbFile smbFile = new SmbFile(sambaUrl);

            if (smbFile.exists()) {
                smbFile.delete();
                return true;
            } else {
                System.err.println("El archivo no existe en Samba.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar el archivo desde Samba: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static List<String> getFileListFromSamba() {
        List<String> fileList = new ArrayList<>();

        try {
            String authString = USERNAME + ":" + PASSWORD + "@";
            String sambaUrl = "smb://" + authString + SAMBA_SERVER.substring(6);
            SmbFile smbFile = new SmbFile(sambaUrl);

            if (smbFile.isDirectory()) {
                SmbFile[] files = smbFile.listFiles();
                for (SmbFile file : files) {
                    fileList.add(file.getName().replace("storage_samba", ""));
                }
            } else {
                System.err.println("La ruta en Samba no es un directorio.");
            }
        } catch (Exception e) {
            System.err.println("Error al obtener la lista de archivos desde Samba: " + e.getMessage());
            e.printStackTrace();
        }
        return fileList;
    }
    private static void uploadToSamba(InputStream fileStream, String fileName) throws Exception {
        // La URL de Samba debe incluir la información de autenticación antes del servidor
        String authString = USERNAME + ":" + PASSWORD + "@";
        String sambaUrl = "smb://" + authString + SAMBA_SERVER.substring(6) + "/" + fileName;
        System.out.println("Samba URL: " + sambaUrl);
        SmbFile smbFile = new SmbFile(sambaUrl);
        try (SmbFileOutputStream smbFileOutputStream = new SmbFileOutputStream(smbFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileStream.read(buffer)) != -1) {
                smbFileOutputStream.write(buffer, 0, bytesRead);
            }
        } catch (jcifs.smb.SmbException e) {
            // Maneja específicamente las excepciones de SmbException
            System.err.println("Error al conectar con el servidor Samba: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // Maneja otras excepciones
            System.err.println("Error al cargar el archivo en Samba: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private static String getFileName(Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
