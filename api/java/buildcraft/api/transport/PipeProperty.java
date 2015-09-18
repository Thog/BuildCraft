package buildcraft.api.transport;

public final class PipeProperty<T> {
    public final String id;
    private final T defaultValue;

    PipeProperty(String id, T defaultValue) {
        this.id = id;
        this.defaultValue = defaultValue;
    }

    static <T> PipeProperty<T> create(String id, T defaultValue) {
        return new PipeProperty<T>(id, defaultValue);
    }

    public T getDefault() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return "PipeProperty [id=" + id + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PipeProperty<?> other = (PipeProperty<?>) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
