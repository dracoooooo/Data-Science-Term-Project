public class AverageSalaryExcludingtheMinimumandMaximumSalary {
    public double average(int[] salary) {
        int sum = 0,min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for(int sal : salary){
            min = Math.min(sal, min);
            max = Math.max(sal, max);
            sum+=sal;
        }
         
        
        
        return (double)(sum-max-min)/(salary.length-2);
    }
}
