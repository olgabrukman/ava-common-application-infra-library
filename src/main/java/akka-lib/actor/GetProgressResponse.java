package actor;

import progress.ProgressDetails;

import java.util.LinkedList;
import java.util.List;

public class GetProgressResponse {
    private List<ProgressDetails> list = new LinkedList<>();

    public void addDetails(ProgressDetails details) {
        list.add(details);
    }

    public List<ProgressDetails> getList() {
        return list;
    }

    @Override
    public String toString() {
        return "GetProgressResponse{" +
                "list=" + list +
                '}';
    }


}
