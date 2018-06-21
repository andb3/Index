package com.andb.apps.todo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InboxFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InboxFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class InboxFragment extends Fragment {

    public static ArrayList<Tasks> blankTaskList = new ArrayList<>();
    public static int filterMode = 0; //0=date, 1=alphabetical, more to come

    public static ArrayList<Tasks> filteredTaskList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    public static InboxAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ActionMode contextualToolbar;
    public boolean selected = false;


    private OnFragmentInteractionListener mListener;

    public InboxFragment() {
        // Required empty public constructor
    }

    public static InboxFragment newInstance() {
        InboxFragment fragment = new InboxFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        Log.d("inflating", "inbox inflating");
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        setFilterMode(filterMode);
        prepareRecyclerView(view);

        if (filteredTaskList.isEmpty()) {
            view.findViewById(R.id.noTasks).setVisibility(View.VISIBLE);
        }

        Log.d("inboxFilterInboxBlank", Integer.toString(filteredTaskList.size()));


        if (filteredTaskList.isEmpty()) {
            view.findViewById(R.id.noTasks).setVisibility(View.VISIBLE);
        }

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                if(mAdapter.getItemViewType(position)==0 & !selected) {
                    contextualToolbar = InboxFragment.this.getActivity().startActionMode(setCallback(position));
                    view.setSelected(true);
                    mAdapter.isSelected = true;
                    mAdapter.notifyItemChanged(position);
                    selected = true;
                }
            }
        }) {
        });

        //hide fab on scroll
        final FloatingActionButton fabMain = (FloatingActionButton) getActivity().findViewById(R.id.fab_main);
        final FloatingActionButton fabList = (FloatingActionButton) getActivity().findViewById(R.id.fab_list);
        final FloatingActionButton fabTag = (FloatingActionButton) getActivity().findViewById(R.id.fab_tag);
        final int scrollSensitivity = 5;
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > scrollSensitivity) {
                    fabMain.hide();
                    fabList.hide();
                    fabTag.hide();

                } else if (dy < scrollSensitivity) {
                    fabMain.show();
                    if (MainActivity.fabOpen) { //checks if extra fabs are open, if yes then return them to view
                        fabList.show();
                        fabTag.show();
                    }
                }
            }
        });

        return view;


    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public void prepareRecyclerView(View view) {


        mRecyclerView = (RecyclerView) view.findViewById(R.id.inboxRecycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new InboxAdapter(filteredTaskList);
        mRecyclerView.setAdapter(mAdapter);

        if (!MainActivity.deleteFromNotif) {
            mRecyclerView.scrollToPosition(filteredTaskList.indexOf(TaskList.getItem(MainActivity.lastItemPos)));
        }
        MainActivity.deleteFromNotif = false;//reset if it is from a done action


    }


    public static void addTask(String title, ArrayList<String> items, ArrayList<Boolean> checked, ArrayList<Integer> tags, DateTime time) {


        Tasks tasks = new Tasks(title, items, checked, tags, time);
        TaskList.addTaskList(tasks);
        Log.d("recyclerCreated", "outer created");

        BrowseFragment.createFilteredTaskList(Filters.getCurrentFilter(), true);
        setFilterMode(filterMode);
    }


    public static void replaceTask(String title, ArrayList<String> items, ArrayList<Boolean> checked, ArrayList<Integer> tags, DateTime time, int position) {


        Tasks tasks = new Tasks(title, items, checked, tags, time);
        TaskList.setTaskList(position, tasks);
        Log.d("recyclerCreated", "outer created");
        BrowseFragment.createFilteredTaskList(Filters.getCurrentFilter(), true);
        InboxFragment.setFilterMode(InboxFragment.filterMode);
        mAdapter.isSelected = false;
        mAdapter.notifyDataSetChanged();
    }


    public static void setFilterMode(int mode) {

        filterMode = mode;

        ArrayList<Tasks> tempList = filteredTaskList;

        Log.d("inboxFilterInbox", Integer.toString(filteredTaskList.size()));

        for (int i = 0; i < tempList.size(); i++) {
            if (tempList.get(i).getListName().equals("OVERDUE") | tempList.get(i).getListName().equals("TODAY") | tempList.get(i).getListName().equals("WEEK") | tempList.get(i).getListName().equals("MONTH") | tempList.get(i).getListName().equals("FUTURE")) {
                Log.d("removing", "removing " + tempList.get(i).getListName());
                filteredTaskList.remove(i);
                i--;
            }
        }

        tempList = filteredTaskList;

        Log.d("inboxFilterInbox", Integer.toString(filteredTaskList.size()));


        if (mode == 0) {


            boolean overdue = true;
            boolean today = true;
            boolean thisWeek = true;
            boolean thisMonth = true;
            boolean future = true;


            Log.d("loopStart", "Size: " + Integer.toString(filteredTaskList.size()));

            for (int i = 0; i < tempList.size(); i++) {
                Log.d("loopStart", "loop through " + Integer.toString(i));
                DateTime taskDateTime = new DateTime(tempList.get(i).getDateTime());
                if (taskDateTime.isBefore(DateTime.now())) {
                    if (overdue) {
                        Log.d("addDivider", "adding OVERDUE from " + tempList.get(i).getListName() + ", " + tempList.get(i).getDateTime().toString());
                        Tasks tasks = new Tasks("OVERDUE", new ArrayList(), new ArrayList(), new ArrayList(), new DateTime(1970, 1, 1, 0, 0));

                        filteredTaskList.add(i, tasks);


                        overdue = false;
                    }
                } else if (taskDateTime.isBefore(DateTime.now().plusDays(1))) {
                    if (today) {
                        Log.d("addDivider", "adding TODAY from " + tempList.get(i).getListName() + ", " + tempList.get(i).getDateTime().toString());
                        Log.d("addDivider", tempList.get(i).getListName());
                        Tasks tasks = new Tasks("TODAY", new ArrayList(), new ArrayList(), new ArrayList(), new DateTime(DateTime.now()));//drop one category to show at top

                        filteredTaskList.add(i, tasks);


                        today = false;
                    }
                } else if (taskDateTime.isBefore(DateTime.now().plusWeeks(1))) {
                    if (thisWeek) {
                        Log.d("addDivider", "adding WEEK from " + tempList.get(i).getListName() + ", " + tempList.get(i).getDateTime().toString() + " at position " + Integer.toString(i));
                        Tasks tasks = new Tasks("WEEK", new ArrayList(), new ArrayList(), new ArrayList(), new DateTime(DateTime.now().plusDays(1)));

                        filteredTaskList.add(i, tasks);


                        thisWeek = false;
                    }
                } else if (taskDateTime.isBefore(DateTime.now().plusMonths(1))) {
                    if (thisMonth) {
                        Log.d("addDivider", "adding MONTH from " + tempList.get(i).getListName() + ", " + tempList.get(i).getDateTime().toString());
                        Tasks tasks = new Tasks("MONTH", new ArrayList(), new ArrayList(), new ArrayList(), new DateTime(DateTime.now().plusWeeks(1)));

                        filteredTaskList.add(i, tasks);

                        thisMonth = false;
                    }

                } else if (taskDateTime.isAfter(DateTime.now().plusMonths(1))) {
                    if (future) {
                        Log.d("addDivider", "adding FUTURE from " + tempList.get(i).getListName() + ", " + tempList.get(i).getDateTime().toString());
                        Tasks tasks = new Tasks("FUTURE", new ArrayList(), new ArrayList(), new ArrayList(), new DateTime(DateTime.now().plusMonths(1)));

                        filteredTaskList.add(i, tasks);


                        future = false;
                    }

                }
            }

            Log.d("inboxFilterInbox", Integer.toString(filteredTaskList.size()));


            Collections.sort(filteredTaskList, new Comparator<Tasks>() {
                @Override
                public int compare(Tasks o1, Tasks o2) {
                    if (o1.getDateTime() == null) {
                        o1.setDateTime(new DateTime(1970, 1, 1, 0, 0, 0));
                    }
                    if (o2.getDateTime() == null) {
                        o2.setDateTime(new DateTime(1970, 1, 1, 0, 0, 0));
                    }

                    return o1.getDateTime().compareTo(o2.getDateTime());
                }
            });

            Log.d("inboxFilterInbox", Integer.toString(filteredTaskList.size()));



        } else if (mode == 1) {


            Collections.sort(filteredTaskList, new Comparator<Tasks>() {
                @Override
                public int compare(Tasks o1, Tasks o2) {
                    return o1.getListName().compareTo(o2.getListName());
                }
            });

            Log.d("inboxFilterInbox", Integer.toString(filteredTaskList.size()));


        }

        //mAdapter.notifyDataSetChanged();





        Log.d("inboxFilterInboxEnd", Integer.toString(filteredTaskList.size()));


    }


    public ActionMode.Callback setCallback(final int position) {
        ActionMode.Callback callback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = new MenuInflater(InboxFragment.this.getContext());
                menuInflater.inflate(R.menu.toolbar_inbox_long_press, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.editTask:
                        Intent editTask = new Intent(InboxFragment.this.getContext(), AddTask.class);
                        editTask.putExtra("edit", true);
                        editTask.putExtra("editPos", position);
                        editTask.putExtra("browse", false);
                        startActivity(editTask);
                        mode.finish();
                        return true;

                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                selected = false;
                mAdapter.isSelected = false;
                mAdapter.notifyItemChanged(position);
            }
        };

        return callback;
    }


}
