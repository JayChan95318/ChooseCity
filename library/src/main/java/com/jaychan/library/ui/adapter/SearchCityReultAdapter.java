package com.jaychan.library.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jaychan.library.R;
import com.jaychan.library.bean.City;
import com.jaychan.library.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;


public class SearchCityReultAdapter extends BaseAdapter {

    private List<City> mSearchCityList = new ArrayList<>();

    public SearchCityReultAdapter(List<City> cities) {
        mSearchCityList = cities;
    }

    @Override
    public int getCount() {
        return mSearchCityList.size();
    }

    @Override
    public City getItem(int position) {
        return mSearchCityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if(convertView==null){
            holder = new ViewHolder();
            convertView = View.inflate(UIUtils.getContext(), R.layout.item_search_city_result,null);
            holder.tvCity = (TextView) convertView.findViewById(R.id.tv_city);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        City city = getItem(position);
        holder.tvCity.setText(city.getName());

        return convertView;
    }

    public void notifyChange(List<City> cities){
        mSearchCityList = cities;
        notifyDataSetChanged();
    }

    static class ViewHolder{
        TextView tvCity;
    }
}
