package hr.fer.zemris.java.raytracer.model;

/**
 * This class extends the {@linkplain GraphicalObject} and provides
 * implementation for the {@linkplain #findClosestRayIntersection(Ray)} method.
 *
 * @author Mario Bobic
 */
public class Sphere extends GraphicalObject {

    /** Sphere center. */
    private Point3D center;
    /** Sphere radius. */
    private double radius;

    /** Diffuse component red. */
    private double kdr;
    /** Diffuse component green. */
    private double kdg;
    /** Diffuse component blue. */
    private double kdb;

    /** Reflective component red. */
    private double krr;
    /** Reflective component green. */
    private double krg;
    /** Reflective component blue. */
    private double krb;

    /** Reflective material factor. */
    private double krn;

    /**
     * Constructs an instance of Sphere with the specified parameters.
     *
     * @param center sphere center
     * @param radius sphere radius
     * @param kdr diffuse component red
     * @param kdg diffuse component green
     * @param kdb diffuse component blue
     * @param krr reflective component red
     * @param krg reflective component green
     * @param krb reflective component blue
     * @param krn reflective material factor
     */
    public Sphere(Point3D center, double radius, double kdr, double kdg, double kdb,
            double krr, double krg, double krb, double krn) {
        this.center = center;
        this.radius = radius;
        this.kdr = kdr;
        this.kdg = kdg;
        this.kdb = kdb;
        this.krr = krr;
        this.krg = krg;
        this.krb = krb;
        this.krn = krn;
    }

    @Override
    public RayIntersection findClosestRayIntersection(Ray ray) {
        Point3D startToCenter = ray.start.sub(center);

        double a = ray.direction.scalarProduct(ray.direction); // always 1
        double b = 2 * ray.direction.scalarProduct(startToCenter);
        double c = startToCenter.scalarProduct(startToCenter) - Math.pow(radius, 2);

        double discriminant = b*b - 4*a*c;
        double distance;
        boolean outer = true;

        // No intersections
        if (discriminant < 0) {
            return null;
        }

        double sqrt = Math.sqrt(discriminant);
        double s1 = (-b + sqrt) / (2*a);
        double s2 = (-b - sqrt) / (2*a);

        // One or two intersections
        double minDistance = Math.min(s1, s2);
        double maxDistance = Math.max(s1, s2);

        // The sphere is behind the ray
        if (minDistance < 0 && maxDistance < 0) {
            return null;
        }

        if (minDistance >= 0) {
            distance = minDistance;
        } else {
            // The ray is inside the sphere
            distance = maxDistance;
            outer = false;
        }

        Point3D intersection = ray.start.add(ray.direction.scalarMultiply(distance));

        return new RayIntersection(intersection, distance, outer) {

            @Override
            public Point3D getNormal() {
                return intersection.sub(center).normalize();
            }

            @Override
            public double getKrr() {
                return krr;
            }

            @Override
            public double getKrn() {
                return krn;
            }

            @Override
            public double getKrg() {
                return krg;
            }

            @Override
            public double getKrb() {
                return krb;
            }

            @Override
            public double getKdr() {
                return kdr;
            }

            @Override
            public double getKdg() {
                return kdg;
            }

            @Override
            public double getKdb() {
                return kdb;
            }

        };
    }

}
