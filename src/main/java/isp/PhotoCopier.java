package isp;

public class PhotoCopier implements MultiFunctionDevice {
    @Override
    public String print(String content) {
        return "printing: " + content;
    }

    @Override
    public String scan(String content) {
        return "scanning: " + content;
    }

    @Override
    public String fax(String content) {
        throw new UnsupportedOperationException("PhotoCopier cannot fax");
    }
}
