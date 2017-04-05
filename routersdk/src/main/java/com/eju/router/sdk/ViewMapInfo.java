package com.eju.router.sdk;

/**
 * view map info
 * related with viewmap.xml
 *
 * @author Sidney
 */
public class ViewMapInfo {

    public static final int TYPE_UNSPECIFIED = -1;
    public static final int TYPE_NATIVE = 0;
    public static final int TYPE_LOCAL_HTML = 1;
    public static final int TYPE_REMOTE_HTML = 2;

    private String id;
    private int type = TYPE_UNSPECIFIED;
    private String resource;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public ViewMapInfo() {
    }

    public ViewMapInfo(String id, int type, String resource,String description) {
        this.id = id;
        this.type = type;
        this.resource = resource;
        this.description = description;
    }

    @Override
    public String toString() {
        return "ViewMapInfo{" +
                "description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", type=" + type +
                ", resource='" + resource + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || !ViewMapInfo.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        ViewMapInfo info = (ViewMapInfo)obj;
        return (this.id.equalsIgnoreCase(info.id)
                && this.type == info.type
                && this.resource.equalsIgnoreCase(info.resource)) || super.equals(info);
    }
}
