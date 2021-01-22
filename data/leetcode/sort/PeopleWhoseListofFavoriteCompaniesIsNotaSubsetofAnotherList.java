public class PeopleWhoseListofFavoriteCompaniesIsNotaSubsetofAnotherList {
    public List<Integer> peopleIndexes(List<List<String>> favoriteCompanies) {
        Map<String, List<Integer>> map = new HashMap<>();
        int len = favoriteCompanies.size();
        for (int i = 0; i < len; i++) {
            List<String> temp = favoriteCompanies.get(i);
            for (String comp : temp) {
                if (!map.containsKey(comp)) map.put(comp, new ArrayList<Integer>());
                map.get(comp).add(i);
            }
        }
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            List<String> comps = favoriteCompanies.get(i);
            if (isValid(comps, map, len, i)) result.add(i);
        }
        return result;
    }
	// check frequency of indexes for every company in a company list
    private boolean isValid(List<String> comps, Map<String, List<Integer>> map, int len, int currIndex) {
        int[] fres = new int[len];
        for (String comp : comps) {
            List<Integer> list = map.get(comp);
            for (Integer index : list) fres[index]++;
        }
        for (int i = 0; i < fres.length; i++) {
            if (fres[i] == comps.size() && i != currIndex) return false;
        }
        return true;
    }
}
