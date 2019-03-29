package hr.fer.zemris.java.fractals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import hr.fer.zemris.java.fractals.viewer.FractalViewer;
import hr.fer.zemris.java.fractals.viewer.IFractalProducer;
import hr.fer.zemris.java.fractals.viewer.IFractalResultObserver;

/**
 * This program first reads the complex polynomial roots from user and stores
 * them into an array of {@linkplain Complex} objects. If a parsing error occurs
 * during the reading, the user will be informed and asked to enter the complex
 * root again.
 * <p>
 * Second, it creates new daemon threads used for running the jobs of fractal
 * image calculation and then the fractal image is finally shown to the user.
 *
 * @author Mario Bobic
 */
public class Newton {

    /** Array of roots fetched from the user. */
    private static Complex[] roots;
    /** The rooted polynomial created from array of roots. */
    private static ComplexRootedPolynomial rootedPolynomial;

    /**
     * Program entry point.
     *
     * @param args not used in this example
     * @throws IOException if an unrecoverable reading error occurs
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to Newton-Raphson iteration-based fractal viewer.");
        System.out.println("Please enter at least two roots, one root per line. Enter 'done' when done.");

        readInput();

        System.out.println("Image of fractal will appear shortly. Thank you.");

        showImage();
    }

    /**
     * Reads the input from user, filling the <tt>roots</tt> array and ignoring
     * empty lines. Every input line is parsed as a complex number using the
     * {@linkplain Complex#parse(String)} method.
     * <p>
     * If the user enters a format that can not be parsed as a complex number, a
     * message is printed out and the user is again prompted to input a complex
     * number.
     *
     * @throws IOException if an unrecoverable reading error occurs
     */
    private static void readInput() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<Complex> rootsList = new ArrayList<>();

        for (int i = 1; ; i++) {
            System.out.print("Root " + i + "> ");

            String line = reader.readLine();
            if (line == null) break;

            line = line.trim();

            if (line.equalsIgnoreCase("done")) break;
            if (line.isEmpty()) {
                i--;
                continue;
            }

            try {
                Complex c = Complex.parse(line);
                rootsList.add(c);
            } catch (NumberFormatException e) {
                System.out.println("Cannot parse \"" + line + "\" as a complex number.");
                i--;
            }
        }

        roots = rootsList.toArray(new Complex[rootsList.size()]);
        rootedPolynomial = new ComplexRootedPolynomial(roots);
        reader.close();
    }

    /**
     * Shows the fractal image of the specified roots.
     */
    private static void showImage() {
        FractalViewer.show(new FractalProducerImpl());
    }

    /**
     * This class is an implementation of the {@linkplain IFractalProducer}. It
     * produces a fractal image by calling the {@linkplain #produce} method for
     * the arguments specified by the method.
     *
     * @author Mario Bobic
     */
    private static class FractalProducerImpl implements IFractalProducer {

        // Static initializers, some are written in lowercase letters for tidiness

        /** Number of processors available to the Java virtual machine. */
        private static final int NUMPROCESSORS = Runtime.getRuntime().availableProcessors();
        /** Number of jobs, determined as NUMPROCESSORS * 8. */
        private static final int NUMJOBS = NUMPROCESSORS * 8;

        /** Maximum number of iterations until the complex calculation stops. */
        private static final int MAX_ITERS = 16 * 16;
        /** The convergence threshold. */
        private static final double convergenceThreshold = 0.001;
        /** The root threshold. */
        private static final double rootThreshold = 0.002;

        /** A polynomial obtained from the rooted polynomial. */
        private static final ComplexPolynomial polynomial = rootedPolynomial.toComplexPolynom();
        /** A derivative of a polynomial obtained from the rooted polynomial. */
        private static final ComplexPolynomial derived = polynomial.derive();


        // Non-static initializers

        /** Thread pool that reuses a fixed number of daemonic threads. */
        private ExecutorService pool =
                Executors.newFixedThreadPool(NUMPROCESSORS, new DaemonicThreadFactory());

        /** List of Future objects containing jobs. */
        private List<Future<Void>> results = new ArrayList<>();

        @Override
        public void produce(double reMin, double reMax, double imMin, double imMax,
                int width, int height, long requestNo, IFractalResultObserver observer) {

            // Initialize settings
            short[] data = new short[width * height];
            int jobHeightFraction = height / NUMJOBS;

            // Submit every job and store Future objects
            for (int i = 0; i < NUMJOBS; i++) {
                int ymin = i * jobHeightFraction;
                int ymax = (i+1) * jobHeightFraction;

                if (i == NUMJOBS - 1) { // last job
                    ymax = height; // collect remnants
                }

                Job job = new Job(reMin, reMax, imMin, imMax, width, height, ymin, ymax, data);

                results.add(pool.submit(job));
            }

            // Wait until all jobs are done
            for (Future<Void> job : results) {
                try {
                    job.get();
                } catch (Exception ignorable) {}
            }

            observer.acceptResult(data, (short)(polynomial.order() + 1), requestNo);
        }

        /**
         * This class implements the {@linkplain Callable} interface and
         * represents a job that fills the array of <tt>short</tt> integers with
         * color indexes until it reached a predefined number of iterations or
         * until the convergence threshold becomes adequately small.
         *
         * @author Mario Bobic
         */
        static class Job implements Callable<Void> {
            /** Minimum value of the real part of a complex number. */
            private double reMin;
            /** Maximum value of the real part of a complex number. */
            private double reMax;
            /** Minimum value of the imaginary part of a complex number. */
            private double imMin;
            /** Maximum value of the imaginary part of a complex number. */
            private double imMax;

            /** Width of the image where this job produces fractals. */
            private int width;
            /** Height of the image where this job produces fractals. */
            private int height;
            /** The starting y coordinate of producing. */
            private int ymin;
            /** The ending y coordinate of producing. */
            private int ymax;

            /** Array that contains color index data. */
            private short[] data;

            /**
             * Constructs an instance of a <tt>Job</tt> object with the
             * specified parameters.
             *
             * @param reMin minimum value of the real part of a complex number
             * @param reMax maximum value of the real part of a complex number
             * @param imMin minimum value of the imaginary part of a complex number
             * @param imMax maximum value of the imaginary part of a complex number
             * @param width width of the image where this job produces fractals
             * @param height height of the image where this job produces fractals
             * @param ymin the starting y coordinate of producing
             * @param ymax the ending y coordinate of producing
             * @param data array where the color index data will be stored
             */
            public Job(double reMin, double reMax, double imMin, double imMax,
                    int width, int height, int ymin, int ymax, short[] data) {
                this.reMin = reMin;
                this.reMax = reMax;
                this.imMin = imMin;
                this.imMax = imMax;
                this.width = width;
                this.height = height;
                this.ymin = ymin;
                this.ymax = ymax;
                this.data = data;
            }

            @Override
            public Void call() {
                int offset = ymin * width;

                for (int y = ymin; y < ymax; y++) {
                    for (int x = 0; x < width; x++) {
                        double cre = x * (reMax - reMin) / (width - 1.0) + reMin;
                        double cim = (height - 1.0 - y) * (imMax - imMin) / (height - 1) + imMin;
                        Complex zn = new Complex(cre, cim);

                        int i = 0;
                        double module;
                        Complex zn1;
                        do {
                            Complex numerator = polynomial.apply(zn);
                            Complex denominator = derived.apply(zn);
                            Complex fraction = numerator.divide(denominator);
                            zn1 = zn.sub(fraction);
                            module = zn1.sub(zn).module();
                            zn = zn1;
                            i++;
                        } while (module > convergenceThreshold && i < MAX_ITERS);

                        short index = (short) rootedPolynomial.indexOfClosestRootFor(zn1, rootThreshold);
                        data[offset++] = (short) (index + 1);
                    }
                }

                return null;
            }

        }

        /**
         * This class implements the {@linkplain ThreadFactory} interface and
         * represents an object that creates new daemon threads on demand.
         * <p>
         * The daemon threads are used so that the program running them can be
         * terminated completely, without waiting for these threads to finish.
         *
         * @author Mario Bobic
         */
        static class DaemonicThreadFactory implements ThreadFactory {

            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setDaemon(true);
                return thread;
            }

        }

    }

}
