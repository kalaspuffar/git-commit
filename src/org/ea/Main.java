package org.ea;

import java.io.*;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.zip.Deflater;

public class Main {
    private final static char[] hexArray = "0123456789abcdef".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


    public static byte[] createObject(File dir, String type, String data) {
        int len = data.length();
        String fileData = type+" "+len + "\0" + data;
        String fileHash = null;
        byte[] digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("sha1");
            digest = md.digest(fileData.getBytes());
            fileHash = bytesToHex(digest);
            System.out.println(fileHash);
            File object_dir = new File(dir, fileHash.substring(0, 2));
            object_dir.mkdirs();
            File object_file = new File(object_dir, fileHash.substring(2));

            byte[] output = new byte[1000];
            Deflater compresser = new Deflater();
            compresser.setInput(fileData.getBytes());
            compresser.finish();
            int compressedDataLength = compresser.deflate(output);
            compresser.end();

            FileOutputStream fos = new FileOutputStream(object_file);
            fos.write(output, 0, compressedDataLength);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return digest;
    }

    public static byte[] createTree(File dir, String mode, String filename, byte[] sha) {
        int len = mode.length() + filename.length() + 20 + 2;
        String fileData = "tree " + len + "\0" + mode + " " + filename + "\0";
        byte[] digest = null;
        String fileHash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("sha1");
            md.update(fileData.getBytes());
            digest = md.digest(sha);
            fileHash = bytesToHex(digest);
            System.out.println(fileHash);
            File object_dir = new File(dir, fileHash.substring(0, 2));
            object_dir.mkdirs();
            File object_file = new File(object_dir, fileHash.substring(2));

            byte[] output = new byte[1000];
            Deflater compresser = new Deflater();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(fileData.getBytes());
            baos.write(sha);
            compresser.setInput(baos.toByteArray());
            compresser.finish();
            int compressedDataLength = compresser.deflate(output);
            compresser.end();

            FileOutputStream fos = new FileOutputStream(object_file);
            fos.write(output, 0, compressedDataLength);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return digest;
    }

    public static void main(String[] args) {
        File directory = new File("c:/tmp/test2");
        File object_dir = new File(directory, ".git/objects");
        object_dir.mkdirs();
        File heads_dir = new File(directory, ".git/refs/heads");
        heads_dir.mkdirs();
        File head = new File(directory, ".git/HEAD");

        try {
            FileWriter fw = new FileWriter(head);
            fw.append("ref:refs/heads/master");
            fw.close();

            byte[] data_hash = createObject(object_dir, "blob", "10 print 'HELLO WORLD';");

            byte[] file_hash = createTree(object_dir, "100644", "test.txt", data_hash);

            String commit_data = "tree "+bytesToHex(file_hash) + "\n";
            commit_data += "author Daniel Persson<author@example.com> 0 +0000\n";
            commit_data += "committer Daniel Persson<committer@example.com> 0 +0000\n\n";
            commit_data += "First commit message.\n\n";

            byte[] commit_hash = createObject(object_dir, "commit", commit_data);


            File master = new File(heads_dir, "master");
            FileWriter fw2 = new FileWriter(master);
            fw2.write(bytesToHex(commit_hash));
            fw2.close();

            /*
            commit 174
            tree 496d6428b9cf92981dc9495211e6e1120fb6f2ba\n
            author Author Name <author@example.com> 0 +0000\n
            committer Committer Name <committer@example.com> 946684800 +0000\n\n
            First message
            \n\n
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
