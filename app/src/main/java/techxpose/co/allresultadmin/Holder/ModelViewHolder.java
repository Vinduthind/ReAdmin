package techxpose.co.allresultadmin.Holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import techxpose.co.allresultadmin.R;

public class ModelViewHolder extends RecyclerView.ViewHolder{
    View mview;
    public ModelViewHolder(@NonNull View itemView) {
        super(itemView);
        mview=itemView;
    }

    public void setExamination(String examination)
    {
        TextView setexamination = mview.findViewById(R.id.examination);
        setexamination.setText(examination);

    }
    public void hideVisibility()
    {
        mview.findViewById(R.id.delete).setVisibility(View.GONE);
        mview.findViewById(R.id.edit).setVisibility(View.GONE);
    }

    public void showVisibility(){

        mview.findViewById(R.id.delete).setVisibility(View.VISIBLE);
        mview.findViewById(R.id.edit).setVisibility(View.VISIBLE);
    }
    public void setdate(String resultDate)
    {
        TextView branch_name = mview.findViewById(R.id.annoucedon);
        branch_name.setText(resultDate);

    }
    public  void setBranchname(String Branchname)
    {
        TextView branch_name = mview.findViewById(R.id.branchname);

        branch_name.setText(Branchname);

    }
    public View getMview() {
        return mview;
    }

    public View getDelete(){
        return mview.findViewById(R.id.delete);
    }
    public  void setResultlink(String resultlink)
    {
        //TextView result_link = (TextView)mview.findViewById(R.id.link);
        //result_link.setText(resultlink);
    }
}
