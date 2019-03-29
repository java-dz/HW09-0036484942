package hr.fer.zemris.java.raytracer;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import hr.fer.zemris.java.raytracer.model.*;
import hr.fer.zemris.java.raytracer.viewer.*;

/**
 * Demonstrates the usage of {@linkplain RayTracerViewer} with an implementation
 * of the {@linkplain IRayTracerProducer}.
 *
 * @author Mario Bobic
 */
public class RayCasterParallel {

    /** Maximum distance offset for intersections. */
    private static final double LIMIT = 1E-6;

    /** Background color. */
    private static final short[] COLOR_EMPTY = {0, 0, 0};

    /** Ambient light color. */
    private static final short[] COLOR_AMBIENT = {15, 15, 15};

    /**
     * Program entry point.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        RayTracerViewer.show(getIRayTracerProducer(),
                new Point3D(10, 0, 0),
                new Point3D(0, 0, 0),
                new Point3D(0, 0, 10),
                20, 20);
    }

    /**
     * Returns an implementation of the {@link IRayTracerProducer ray tracer
     * producer}.
     *
     * @return an implementation of the ray tracer producer
     */
    private static IRayTracerProducer getIRayTracerProducer() {
        return new IRayTracerProducer() {

            @Override
            public void produce(Point3D eye, Point3D view, Point3D viewUp,
                    double horizontal, double vertical, int width, int height,
                    long requestNo, IRayTracerResultObserver observer) {

                System.out.println("Započinjem izračune...");

                short[] red = new short[width * height];
                short[] green = new short[width * height];
                short[] blue = new short[width * height];

                // normalize the viewUp
                viewUp = viewUp.normalize();

                // OG vector
                Point3D zAxis = determineZAxis(eye, view);
                // j unit vector
                Point3D yAxis = determineYAxis(zAxis, viewUp);
                // i unit vector
                Point3D xAxis = determineXAxis(zAxis, yAxis);
                // upper-left corner
                Point3D screenCorner = determineScreenCorner(view, xAxis, yAxis, horizontal, vertical);

                Scene scene = RayTracerViewer.createPredefinedScene();

                /**
                 * This class is an implementation of {@linkplain RecursiveAction}
                 * that splits the job of calculation into smaller pieces which are
                 * then done independently.
                 *
                 * @author Mario Bobic
                 *
                 */
                class CalculationAction extends RecursiveAction {
                    /** Serialization UID. */
                    private static final long serialVersionUID = 1L;

                    /** Amount of lines until recursion is stopped. */
                    private static final int LINE_TRESHOLD = 20;

                    /** The starting y coordinate. */
                    private int ymin;
                    /** The ending y coordinate. */
                    private int ymax;

                    /**
                     * Constructs an instance of <tt>PartialCalculation</tt>
                     * with the specified parameters.
                     *
                     * @param ymin the starting y coordinate of the job
                     * @param ymax the ending y coordinate of the job
                     */
                    public CalculationAction(int ymin, int ymax) {
                        this.ymin = ymin;
                        this.ymax = ymax;
                    }

                    @Override
                    public void compute() {
                        if (ymax - ymin <= LINE_TRESHOLD) {
                            computeDirect();
                            return;
                        }

                        int linesPerJob = (ymax - ymin) / 2;
                        invokeAll(
                            new CalculationAction(ymin, ymin + linesPerJob),
                            new CalculationAction(ymin + linesPerJob, ymax)
                        );
                    }

                    /**
                     * Directly computes specified range of coordinates.
                     */
                    private void computeDirect() {
                        short[] rgb = new short[3];
                        int offset = ymin * width;

                        for(int y = ymin; y < ymax; y++) {
                            for(int x = 0; x < width; x++) {
                                Point3D screenPoint = determineScreenPoint(
                                    screenCorner, x, y, xAxis, yAxis, width, height, horizontal, vertical
                                );
                                Ray ray = Ray.fromPoints(eye, screenPoint);

                                tracer(scene, ray, rgb);

                                red[offset] = rgb[0] > 255 ? 255 : rgb[0];
                                green[offset] = rgb[1] > 255 ? 255 : rgb[1];
                                blue[offset] = rgb[2] > 255 ? 255 : rgb[2];

                                offset++;
                            }
                        }
                    }

                };

                ForkJoinPool pool = new ForkJoinPool();
                pool.invoke(new CalculationAction(0, height));
                pool.shutdown();

                System.out.println("Izračuni gotovi...");
                observer.acceptResult(red, green, blue, requestNo);
                System.out.println("Dojava gotova...");
            }

            /**
             * Determines and returns a {@linkplain Point3D} object which
             * represents the <tt>z axis</tt>. The <tt>z axis</tt> is a vector
             * from point <tt>O</tt> to point <tt>G</tt>, that is, the
             * <tt>OG</tt> vector in a normalized form.
             * <p>
             * The <tt>OG</tt> vector mentioned above is a vector that goes from
             * the <tt>eye</tt> to a perpendicular plane called <tt>view</tt>.
             *
             * @param eye point of the eye
             * @param view point of the perpendicular plane
             * @return a vector representing the <tt>z axis</tt>
             */
            private Point3D determineZAxis(Point3D eye, Point3D view) {
                return view.sub(eye).normalize();
            }

            /**
             * Determines and returns a {@linkplain Point3D} object which
             * represents the <tt>y axis</tt>. The <tt>y axis</tt> is a vector
             * obtained by subtracting the <tt>OG</tt> vector (<tt>zAxis</tt>)
             * scalar multiplied with the <tt>viewUp</tt> vector from the
             * <tt>viewUp</tt> vector, that is, a the <tt>j</tt> vector in a
             * normalized form.
             * <p>
             * The <tt>j</tt> vector mentioned above is a vector that represents
             * an axis on the plane called <tt>view</tt>.
             *
             * @param zAxis the <tt>OG</tt> vector
             * @param viewUp the customary view up vector
             * @return a vector representing the <tt>y axis</tt>
             */
            private Point3D determineYAxis(Point3D zAxis, Point3D viewUp) {
                return viewUp.sub(
                    zAxis.scalarMultiply(zAxis.scalarProduct(viewUp))
                ).normalize();
            }

            /**
             * Determines and returns a {@linkplain Point3D} object which
             * represents the <tt>x axis</tt>. The <tt>x axis</tt> is a vector
             * obtained by making a vector product of the specified
             * <tt>zAxis</tt> with the <tt>yAxis</tt>, that is, the <tt>i</tt>
             * vector in a normalized form.
             * <p>
             * The <tt>i</tt> vector mentioned above is a vector that represents
             * an axis on the plane called <tt>view</tt>
             *
             * @param zAxis the <tt>OG</tt> vector
             * @param yAxis the <tt>j</tt> vector
             * @return a vector representing the <tt>x axis</tt>
             */
            private Point3D determineXAxis(Point3D zAxis, Point3D yAxis) {
                return zAxis.vectorProduct(yAxis).normalize();
            }

            /**
             * Determines and returns the upper-left corner of the screen from
             * the specified arguments.
             *
             * @param view point of the perpendicular plane
             * @param xAxis the <tt>i</tt> vector
             * @param yAxis the <tt>j</tt> vector
             * @param horizontal horizontal width of observed space
             * @param vertical vertical height of observed space
             * @return the upper-left corner of the screen
             */
            private Point3D determineScreenCorner(Point3D view, Point3D xAxis, Point3D yAxis,
                    double horizontal, double vertical) {
                return view    .sub(xAxis.scalarMultiply(horizontal / 2))
                              .add(yAxis.scalarMultiply(vertical   / 2));
            }

            /**
             * Determines and returns a point on the screen from the specified
             * arguments.
             *
             * @param screenCorner the upper-left corner of the screen
             * @param x x coordinate of the plane
             * @param y y coordinate of the plane
             * @param xAxis the <tt>i</tt> vector
             * @param yAxis the <tt>j</tt> vector
             * @param width width of the plane
             * @param height height of the plane
             * @param horizontal horizontal width of observed space
             * @param vertical vertical height of observed space
             * @return a point on the screen
             */
            private Point3D determineScreenPoint(Point3D screenCorner, int x, int y, Point3D xAxis, Point3D yAxis,
                    double width, double height, double horizontal, double vertical) {
                double xPos = x / (width - 1.0) * horizontal;
                double yPos = y / (height - 1.0) * vertical;

                return screenCorner    .add(xAxis.scalarMultiply(xPos))
                                    .sub(yAxis.scalarMultiply(yPos));
            }
        };
    }

    /**
     * Traces the whole scene with the specified <tt>ray</tt> and fills the
     * <tt>rgb</tt> array of <tt>short</tt> integers with result colors.
     *
     * @param scene scene to be traced
     * @param ray ray which is used for tracing
     * @param rgb array to which the traced colors will be stored
     */
    private static void tracer(Scene scene, Ray ray, short[] rgb) {
        RayIntersection intersection = getClosestIntersection(scene, ray);

        if (intersection == null) {
            setColor(COLOR_EMPTY, rgb);
        } else {
            setColor(determineSceneColor(scene, ray, intersection), rgb);
        }
    }

    /**
     * Returns the closest intersection of the specified <tt>ray</tt> with an
     * object in the specified <tt>scene</tt>.
     *
     * @param scene scene in which the lights and objects are located
     * @param ray ray for which the closest intersection is to be returned
     * @return the closest intersection of the ray with an object in scene
     */
    private static RayIntersection getClosestIntersection(Scene scene, Ray ray) {
        RayIntersection closest = null;

        for (GraphicalObject go : scene.getObjects()) {
            RayIntersection intersection = go.findClosestRayIntersection(ray);
            if (intersection == null) {
                continue;
            }

            if (closest == null || intersection.getDistance() < closest.getDistance()) {
                closest = intersection;
            }
        }

        return closest;
    }

    /**
     * Copies the data from the specified <tt>source</tt> array to the specified
     * <tt>destination</tt> array. The specified arrays must be of the same
     * length.
     *
     * @param source the source array
     * @param destination the destination array
     * @throws IllegalArgumentException if <tt>source.length != destination.length</tt>
     */
    private static void setColor(short[] source, short[] destination) {
        if (source.length != destination.length) {
            throw new IllegalArgumentException("Both arrays must be of the same length.");
        }

        System.arraycopy(source, 0, destination, 0, source.length);
    }

    /**
     * Determines the scene color depending on the specified ray intersection
     * with the object.
     *
     * @param scene scene in which the lights and objects are located
     * @param fromEye ray that comes from the eye
     * @param intersection intersection for which the color is to be determined
     * @return an array of colors determined from the scene
     */
    private static short[] determineSceneColor(Scene scene, Ray fromEye, RayIntersection intersection) {
        short[] color = COLOR_AMBIENT.clone();

        for (LightSource lightSource : scene.getLights()) {
            Ray fromSource = Ray.fromPoints(
                lightSource.getPoint(),
                intersection.getPoint()
            );

            RayIntersection closestIntersection = getClosestIntersection(
                scene, fromSource
            );

            // if the intersection does not exist
            if (closestIntersection == null) {
                continue;
            }

            double closestDistance = lightSource.getPoint().sub(closestIntersection.getPoint()).norm();
            double targetDistance = lightSource.getPoint().sub(intersection.getPoint()).norm();

            // If closest object is not closest to the light (it is covered)
            if (Math.abs(closestDistance - targetDistance) > LIMIT) {
                continue;
            }

            short[] components = calculateLightComponents(lightSource, intersection, fromEye);

            color[0] += components[0];
            color[1] += components[1];
            color[2] += components[2];
        }

        return color;
    }

    /**
     * Calculates both of the light components (diffuse and reflective) based on
     * the specified parameters <tt>light source</tt>, <tt>ray intersection</tt>
     * and the <tt>ray from eye</tt>. Returns an array of red, green and blue
     * colors which are represented by the <tt>short</tt> integer.
     *
     * @param lightSource the light source
     * @param intersection intersection of the light and object
     * @param fromEye ray that comes from the eye
     * @return an array of red, green and blue colors
     */
    private static short[] calculateLightComponents(LightSource lightSource,
            RayIntersection intersection, Ray fromEye) {

        Point3D sourceVector = lightSource.getPoint().sub(intersection.getPoint()).normalize();
        Point3D normalVector = intersection.getNormal();

        double diffusionFactor = sourceVector.scalarProduct(normalVector);
        diffusionFactor = Math.max(diffusionFactor, 0);

        Point3D reflectionVector = sourceVector.sub(
                normalVector.scalarMultiply(diffusionFactor)
                            .scalarMultiply(2)
        );

        double reflectionFactor = fromEye.direction.scalarProduct(reflectionVector);
        reflectionFactor = Math.max(reflectionFactor, 0);
        reflectionFactor = Math.pow(reflectionFactor, intersection.getKrn());

        // sorry for the long line, looks messy when broken
        short r = (short) (lightSource.getR() * (intersection.getKdr() * diffusionFactor + intersection.getKrr() * reflectionFactor));
        short g = (short) (lightSource.getG() * (intersection.getKdg() * diffusionFactor + intersection.getKrg() * reflectionFactor));
        short b = (short) (lightSource.getB() * (intersection.getKdb() * diffusionFactor + intersection.getKrb() * reflectionFactor));

        return new short[] {r, g, b};
    }

}
