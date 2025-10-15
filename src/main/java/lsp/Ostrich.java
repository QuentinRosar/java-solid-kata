package lsp;

public class Ostrich extends Bird {
    @Override
    public String fly() {
        throw new UnsupportedOperationException("Ostrich can't fly");
    }
}
