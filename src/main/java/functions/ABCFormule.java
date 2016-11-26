/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functions;

/**
 *
 * @author woutermkievit
 */
public class ABCFormule implements Function{

    @Override
    public int doOperation(double m, double a, double b, double c) {
        return (int)Math.round(a * Math.pow(m, 2) + b * m - c);
    }

    
    
}
