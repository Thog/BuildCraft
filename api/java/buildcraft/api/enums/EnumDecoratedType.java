package buildcraft.api.enums;

public enum EnumDecoratedType {
    DESTROY(0),
    BLUEPRINT(10),
    TEMPLATE(10),
    PAPER(10),
    LEATHER(10);

    public final int lightValue;

    private EnumDecoratedType(int lightValue) {
        this.lightValue = lightValue;
    }
}
