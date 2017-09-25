/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import model.PerformanceType;

/**
 * Formula: a*x^2+b*x+c
 * Iaaf: IaafA*(x-IaafB)^2+IaafC OR IaafA*(IaafB-t)^2+IaafC
 * @author woutermkievit, Maxim Moinat
 */
public class IaafFunction {

    private double a;
    private double b;
    private double c;

    public IaafFunction(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    public double getIaafA() {
        return a;
    }

    public double getIaafB() {
        // bi = b/2a
        return b /(2* a);
    }

    public double getIaafC() {
        // c = c - b^2/4a
        return c - Math.pow(b,2)/(4 * a);
    }

    public double calculatePoints(double performance) {
        return a * Math.pow(performance,2) + b * performance + c;
    }

    /**
     * Calculate the performance by solving a*x^2+b*x+c=points for x.
     * Only positive x is a valid performance
     * @param points
     * @return
     * @throws NumberFormatException
     */
    public double calculatePerformance(double points) throws NumberFormatException {
        // Find root of a*x^2+b*x+[c-points]
        double cRoot = c - points;
        double discriminant = Math.pow(b,2) - 4*a*cRoot;
        if (discriminant < 0) {
            throw new NumberFormatException("Points give a non-real performance. Are the constants correct?");
        }

        return (-b+Math.sqrt(discriminant))/(2*a);
    }

    public String toString(PerformanceType performanceType) {
        if (performanceType.equals(PerformanceType.TIME)) {
            return String.format("%9.6f (%12.6f - t)^2 %s %12.6f",
                    getIaafA(),
                    getIaafB(),
                    getIaafC() > 0 ? '+' : '-',
                    Math.abs(getIaafC())
            );
        } else {
            return String.format("%9.6f (x %s %12.6f)^2 %s %12.6f",
                    getIaafA(),
                    getIaafB() > 0 ? '+' : '-',
                    Math.abs(getIaafB()),
                    getIaafC() > 0 ? '+' : '-',
                    Math.abs(getIaafC())
            );
        }
    }
}
