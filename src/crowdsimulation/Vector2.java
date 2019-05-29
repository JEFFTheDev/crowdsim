package crowdsimulation;

public class Vector2 {

    double x;
    double z;

    Vector2(double x, double z) {
        this.x = x;
        this.z = z;
    }

    public String toString() {
        return x + ", " + z;
    }
}
