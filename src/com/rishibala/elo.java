package com.rishibala;

import java.util.List;

class elo {

    private static final double TAU = 0.5;
    private static final double GLICKO_SCALE = 173.7178;
    private static final double DEFAULT_ELO = 1500.0;
    private static final double DEFAULT_RD = 350.0;
    private static final double DEFAULT_VOLATILITY = 0.06;

    // Convert elo and RD to Glicko-2 scale
    private static double convertElo(double elo) {
        return (elo - 1500) / GLICKO_SCALE;
    }

    private static double convertRD(double rd) {
        return rd / GLICKO_SCALE;
    }

    // Convert back to original scale
    private static double convertEloBack(double elo) {
        return elo * GLICKO_SCALE + 1500;
    }

    private static double convertRDBack(double rd) {
        return rd * GLICKO_SCALE;
    }

    public static void calculateGlicko(List<driver> drivers) {
        for (driver currentDriver : drivers) {
            double r = convertElo(currentDriver.getElo());
            double rd = convertRD(currentDriver.getRatingDeviation());
            double sigma = currentDriver.getVolatility();

            double v = 0.0;
            double delta = 0.0;
            double deltaSum = 0.0;
            double vSum = 0.0;

            for (driver opponent : drivers) {
                if (!currentDriver.equals(opponent)) {
                    double rj = convertElo(opponent.getElo());
                    double rdj = convertRD(opponent.getRatingDeviation());

                    double g = 1 / Math.sqrt(1 + 3 * Math.pow(rdj, 2) / Math.pow(Math.PI, 2));
                    double E = 1 / (1 + Math.exp(-g * (r - rj)));

                    deltaSum += g * (1 - E);
                    vSum += Math.pow(g, 2) * E * (1 - E);
                }
            }
            v = 1 / vSum;
            double deltaOverV = deltaSum * v;

            // Update sigma
            double a = Math.log(Math.pow(sigma, 2));
            double A = a;
            double B;
            if (Math.pow(delta, 2) > Math.pow(rd, 2) + v) {
                B = Math.log(Math.pow(delta, 2) - Math.pow(rd, 2) - v);
            } else {
                double k = 1;
                while (f(a - k * TAU, delta, rd, v) < 0) {
                    k++;
                }
                B = a - k * TAU;
            }

            double fA = f(A, delta, rd, v);
            double fB = f(B, delta, rd, v);

            while (Math.abs(B - A) > 0.000001) {
                double C = A + (A - B) * fA / (fB - fA);
                double fC = f(C, delta, rd, v);
                if (fC * fB < 0) {
                    A = B;
                    fA = fB;
                } else {
                    fA /= 2;
                }
                B = C;
                fB = fC;
            }

            sigma = Math.exp(A / 2);

            // Update RD
            double rdPrime = 1 / Math.sqrt(1 / Math.pow(rd, 2) + 1 / v);

            // Update elo
            r += deltaOverV / (1 / Math.pow(rd, 2) + 1 / v);

            currentDriver.setElo(convertEloBack(r));
            currentDriver.setRatingDeviation(convertRDBack(rdPrime));
            currentDriver.setVolatility(sigma);
        }
    }

    private static double f(double x, double delta, double rd, double v) {
        double eX = Math.exp(x);
        double part1 = (Math.pow(delta, 2) - Math.pow(rd, 2) - v - eX) / (2 * Math.pow(eX, 2));
        double part2 = (x - Math.log(Math.pow(delta, 2) - Math.pow(rd, 2) - v)) / Math.pow(TAU, 2);
        return part1 - part2;
    }

//    private static final int K = 32;
//
//    public static void calculateElo(List<driver> drivers) {
//        for (int i = 0; i < drivers.size(); i++) {
//            driver currentDriver = drivers.get(i);
//            for (int j = i + 1; j < drivers.size(); j++) {
//                driver opponent = drivers.get(j);
//                double expectedScoreCurrent = 1.0 / (1 + Math.pow(10, (opponent.getElo() - currentDriver.getElo()) / 400.0));
//                double expectedScoreOpponent = 1 - expectedScoreCurrent;
////                double actualScoreCurrent = (i == 0) ? 1.0 : 0.0;
////                double actualScoreOpponent = 1 - actualScoreCurrent;
//                double actualScoreCurrent = 1;
//                double actualScoreOpponent = 0;
//                double newRatingCurrent = currentDriver.getElo() + K * (actualScoreCurrent - expectedScoreCurrent);
//                double newRatingOpponent = opponent.getElo() + K * (actualScoreOpponent - expectedScoreOpponent);
//                currentDriver.setElo(newRatingCurrent);
//                opponent.setElo(newRatingOpponent);
//            }
//        }
//    }

//    public static void main(String[] args) {
        // examples
//        double driverA = 1500;
//        double driverB = 1542;
//
//        double expectedScoreA = expectedScore(driverA, driverB);
//        double expectedScoreB = expectedScore(driverB, driverA);
//
//
//        double actualScoreA = 1; //A beats B
//        double actualScoreB = 0;
//
//        driverA = updateRating(driverA, expectedScoreA, actualScoreA);
//        driverB = updateRating(driverB, expectedScoreB, actualScoreB);
//
//        System.out.println("New rating for Driver A: " + driverA);
//        System.out.println("New rating for Driver B: " + driverB);



//    }
}



