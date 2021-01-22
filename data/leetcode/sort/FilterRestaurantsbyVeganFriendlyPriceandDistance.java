public class FilterRestaurantsbyVeganFriendlyPriceandDistance {
    public List<Integer> filterRestaurants(int[][] restaurants, int veganFriendly, int maxPrice, int maxDistance) {
        List<int[]> list = new ArrayList<>();
        //Remove all restaurents that not satisfying conditions
        for(int[] arr: restaurants) {
            if((veganFriendly == arr[2] || veganFriendly == 0) 
              && maxPrice >= arr[3] && maxDistance >= arr[4]) {
                list.add(arr);
            }
        }
        
        //Sort valid restaurents
        Collections.sort(list, (a,b) -> 
            (a[1]==b[1] ? b[0]-a[0] : b[1]-a[1])
        );
        
        List<Integer> result = new ArrayList<>();
        for(int[] arr: list) {
            result.add(arr[0]);
        }
        
        return result;
    }
}
