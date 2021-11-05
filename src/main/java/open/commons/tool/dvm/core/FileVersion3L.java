/*
 *
 * This file is generated under this project, "DocVersionManager".
 *
 * Date  : 2014. 11. 26. 오전 11:56:09
 *
 * Author: Park_Jun_Hong_(parkjunhong77@gmail.com)
 * 
 */

package open.commons.tool.dvm.core;

import open.commons.utils.StringUtils;

public class FileVersion3L implements Comparable<FileVersion3L> {

    private Integer major;
    private Integer middle;
    private Integer minor;

    public FileVersion3L() {
        this(0, 0, 0);
    }

    public FileVersion3L(int... versions) {
        update(versions);
    }

    public FileVersion3L(Integer major) {
        this(major, 0, 0);
    }

    public FileVersion3L(Integer... versions) {
        update(versions);
    }

    public FileVersion3L(Integer major, Integer middle) {
        this(major, middle, 0);
    }

    public FileVersion3L(Integer major, Integer middle, Integer minor) {
        setVersion0(major, middle, minor);
    }

    public FileVersion3L(String version) {
        update(version);
    }

    @Override
    public int compareTo(FileVersion3L o) {
        // check 'major'
        int c = this.major - o.major;
        if (c != 0) {
            return c;
        }

        // check 'middle'
        c = this.middle - o.middle;
        if (c != 0) {
            return c;
        }

        // check 'minor'
        return this.minor - o.minor;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FileVersion3L other = (FileVersion3L) obj;
        if (major == null) {
            if (other.major != null)
                return false;
        } else if (!major.equals(other.major))
            return false;
        if (middle == null) {
            if (other.middle != null)
                return false;
        } else if (!middle.equals(other.middle))
            return false;
        if (minor == null) {
            if (other.minor != null)
                return false;
        } else if (!minor.equals(other.minor))
            return false;
        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((major == null) ? 0 : major.hashCode());
        result = prime * result + ((middle == null) ? 0 : middle.hashCode());
        result = prime * result + ((minor == null) ? 0 : minor.hashCode());
        return result;
    }

    public void reset() {
        major = 0;
        middle = 0;
        minor = 0;
    }

    private void setVersion0(Integer major, Integer middle, Integer minor) {
        if (major == null) {
            throw new IllegalArgumentException("'major' MUST NOT BE null. major: " + major + ", middle: " + middle + ", minor: " + middle);
        }
        this.major = major;

        if (minor != null && middle == null) {
            throw new IllegalArgumentException("If 'minor' is set, 'middle' MUST NOT BE null. major: " + major + ", middle: " + middle + ", minor: " + middle);
        }

        this.middle = middle;
        this.minor = minor;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(major);

        if (middle == null) {
            return sb.toString();
        }

        sb.append("." + middle);
        if (minor != null) {
            sb.append("." + minor);
        }

        return sb.toString();
    }

    public void update(int... versions) {
        Integer maj = 0;
        Integer mid = 0;
        Integer min = 0;
        switch (versions.length) {
            case 0:
                break;
            case 1:
                maj = versions[0];
                break;
            case 2:
                maj = versions[0];
                mid = versions[1];
                break;
            default:
                maj = versions[0];
                mid = versions[1];
                min = versions[2];
                break;
        }

        setVersion0(maj, mid, min);
    }

    public void update(Integer major) {
        setVersion0(major, 0, 0);
    }

    public void update(Integer... versions) {
        Integer maj = 0;
        Integer mid = 0;
        Integer min = 0;
        switch (versions.length) {
            case 0:
                break;
            case 1:
                maj = versions[0];
                break;
            case 2:
                maj = versions[0];
                mid = versions[1];
                break;
            default:
                maj = versions[0];
                mid = versions[1];
                min = versions[2];
                break;
        }

        setVersion0(maj, mid, min);
    }

    public void update(Integer major, Integer middle) {
        setVersion0(major, middle, 0);
    }

    public void update(Integer major, Integer middle, Integer minor) {
        setVersion0(major, middle, minor);
    }

    public void update(String version) {
        String[] versions = StringUtils.split(version, ".", true);

        Integer maj = 0;
        Integer mid = 0;
        Integer min = 0;
        switch (versions.length) {
            case 0:
                maj = null;
                break;
            case 1:
                maj = Integer.parseInt(versions[0]);
                break;
            case 2:
                maj = Integer.parseInt(versions[0]);
                mid = Integer.parseInt(versions[1]);
                break;
            default:
                maj = Integer.parseInt(versions[0]);
                mid = Integer.parseInt(versions[1]);
                min = Integer.parseInt(versions[2]);
                break;
        }

        setVersion0(maj, mid, min);
    }

    public void updateValue(VerIdx idx, Integer value) {
        switch (idx) {
            case MAJOR:
                setVersion0(value, middle, minor);
                break;
            case MIDDLE:
                setVersion0(major, value, minor);
                break;
            case MINOR:
                setVersion0(major, middle, value);
                break;
        }
    }

    public enum VerIdx {
        MAJOR(0), MIDDLE(1), MINOR(2);

        int index;

        private VerIdx(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }
}
