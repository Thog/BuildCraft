package buildcraft.core.guide;

public abstract class GuidePartFactory<T extends GuidePart> {
    public abstract T createNew();
}
