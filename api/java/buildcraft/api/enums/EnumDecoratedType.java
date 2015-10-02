package buildcraft.api.enums;

public enum EnumDecoratedType {
    DESTROY(0),
    BLUEPRINT(10),
    TEMPLATE(10),
    PAGE(10),
    BACKING(10);

    public final int lightValue;

    private EnumDecoratedType(int lightValue) {
        this.lightValue = lightValue;
    }
}
