package ru.mavesoft.mgithubbrowser;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.mavesoft.mgithubbrowser.githubaccess.Repository;

public class RepoAdapter extends RecyclerView.Adapter<RepoAdapter.ViewHolder> {

    private int mExpandedPosition = -1;
    private int previousExpandedPosition = 0;

    private List<Repository> localRepositories;

    public RepoAdapter() {
        localRepositories = new ArrayList<>();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvDescription;

        private final LinearLayout repoLayout;
        private final LinearLayout repoDetails;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            tvName = view.findViewById(R.id.tvName);
            tvDescription = view.findViewById(R.id.tvDescription);
            repoLayout = view.findViewById(R.id.repoLayout);
            repoDetails = view.findViewById(R.id.repoDetails);
        }

        public TextView getTvName() {
            return tvName;
        }
        public TextView getTvDescription() {
            return tvDescription;
        }
        public LinearLayout getRepoLayout() {
            return repoLayout;
        }
        public LinearLayout getRepoDetails() {
            return repoDetails;
        }
    }

    public RepoAdapter(List<Repository> repositoryList) {
        localRepositories = repositoryList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_repo_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        int maxNameLength = 30;
        int maxDescriptionLength = 120;

        String currRepoName = localRepositories.get(position).getName();
        String currRepoDescription = localRepositories.get(position).getDescription();

        if (currRepoName.length() > maxNameLength) {
            currRepoName = currRepoName.substring(0, maxNameLength);
        }

        if (currRepoDescription != null && currRepoDescription.length() > maxDescriptionLength) {
            currRepoDescription = currRepoDescription.substring(0, maxDescriptionLength);
        }
        if (currRepoDescription == null) {
            currRepoDescription = viewHolder.itemView
                                  .getResources().getString(R.string.repo_no_description);
        }

        final boolean isExpanded = viewHolder.getAdapterPosition() == mExpandedPosition;
        viewHolder.repoDetails.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        viewHolder.itemView.setActivated(isExpanded);
        if (isExpanded) {
            previousExpandedPosition = viewHolder.getAdapterPosition();
        }

        viewHolder.repoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExpandedPosition = isExpanded ? -1 : viewHolder.getAdapterPosition();
                notifyItemChanged(previousExpandedPosition);
                notifyItemChanged(viewHolder.getAdapterPosition());
            }
        });

        viewHolder.getTvName().setText(currRepoName);
        viewHolder.getTvDescription().setText(currRepoDescription);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localRepositories.size();
    }

    public List<Repository> getLocalRepositories() {
        return localRepositories;
    }

    public void setDataList(List<Repository> dataList) {
        localRepositories = dataList;
    }
}


