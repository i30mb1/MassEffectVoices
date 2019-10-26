package n7.mev.main;

import java.io.File;

import androidx.databinding.ObservableBoolean;

public class SoundModel {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SoundModel that = (SoundModel) o;

        if (id != that.id) return false;
        if (playing != null ? !playing.equals(that.playing) : that.playing != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return path != null ? path.equals(that.path) : that.path == null;
    }

    @Override
    public int hashCode() {
        int result = playing != null ? playing.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + id;
        return result;
    }

    public ObservableBoolean playing = new ObservableBoolean(false);
    private String name;
    private String path;
    private int id;

    public SoundModel(String name, String hero, int id) {
        this.path = hero + File.separator + name;
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
