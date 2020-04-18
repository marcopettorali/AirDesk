package airdesk;

import java.io.*;

public class FileBean implements Serializable {

    private String path;
    private String name;
    private String size;

    public FileBean(String path, String name, String size) {
        this.path = path;
        this.name = name;
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

}
