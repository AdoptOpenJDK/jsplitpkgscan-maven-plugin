package org.adoptopenjdk.maven.plugins;

class ModuleDetail {
    private final int entryCount;
    private final String uri;

    public ModuleDetail(int entryCount, String uri) {
        this.entryCount = entryCount;
        this.uri = uri;
    }

    String uri() {
        return uri;
    }

    int entryCount() {
        return entryCount;
    }

    @Override
    public String toString() {
        return entryCount + " " + uri;
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + entryCount;
        hashCode = 31 * hashCode + uri.hashCode();
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof ModuleDetail)) {
            return false;
        }
        ModuleDetail other = (ModuleDetail) obj;
        return entryCount == other.entryCount && uri.equals(other.uri);
    }
}

