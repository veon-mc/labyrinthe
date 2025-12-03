package g64889.dev3.labyrinthe.model;

public class Objective {
    private String name;

    public Objective(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Objective{" + name + '}';
    }
    }
