package isp;

public class OldPrinter implements MultiFunctionDevice {
    @Override
    public String print(String content) {
        return "printing: " + content;
    }

    @Override
    public String scan(String content) {
        throw new UnsupportedOperationException("OldPrinter cannot scan");
    }

    @Override
    public String fax(String content) {
        throw new UnsupportedOperationException("OldPrinter cannot fax");
    }
}
