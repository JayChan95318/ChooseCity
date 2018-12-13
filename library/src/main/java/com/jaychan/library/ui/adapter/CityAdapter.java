package com.jaychan.library.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jaychan.library.R;
import com.jaychan.library.bean.City;
import com.jaychan.library.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JayChan
 * @date 2018/12/13  15:23
 */
public class CityAdapter extends BaseAdapter {

    private List<City> mCityList = new ArrayList<>();

    public CityAdapter(List<City> cities) {
        mCityList = cities;
    }

    @Override
    public int getCount() {
        return mCityList.size();
    }

    @Override
    public City getItem(int position) {
        return mCityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(UIUtils.getContext(), R.layout.item_city_list, null);
            holder.tvLetter = (TextView) convertView.findViewById(R.id.tv_letter);
            holder.tvCity = (TextView) convertView.findViewById(R.id.tv_city);
            holder.vLine1 = convertView.findViewById(R.id.v_line);
            holder.vLine2 = convertView.findViewById(R.id.v_line2);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        City city = mCityList.get(position);
        String str = "";
        String currentLetter = city.getPinyin().charAt(0) + "";// 当前的首字母

        if (position == 0) {
            str = currentLetter;
        } else {
            String preLetter = mCityList.get(position - 1).getPinyin().charAt(0) + "";// 上一个的首字母
            if (!preLetter.equalsIgnoreCase(currentLetter)) {// 如果和上一个的首字母不相同则显示字母栏
                str = currentLetter;
            }
        }

        if (TextUtils.isEmpty(str)) {// 根据str是否为空决定字母栏是否显示
            holder.tvLetter.setVisibility(View.GONE);
            holder.vLine1.setVisibility(View.VISIBLE);
        }else{
            holder. tvLetter.setVisibility(View.VISIBLE);
            holder.vLine1.setVisibility(View.GONE);
            holder. tvLetter.setText(currentLetter.toUpperCase());
        }

        if (position != mCityList.size() - 1) {
            String nextLetter = mCityList.get(position + 1).getPinyin().charAt(0) + "";// 下一个的首字母
        }

        holder.vLine2.setVisibility(position==mCityList.size()-1?View.VISIBLE:View.GONE);

        holder.tvCity.setText(city.getName());

        return convertView;
    }


    static class ViewHolder {
        TextView tvLetter;
        TextView tvCity;
        View vLine1;
        View vLine2;
    }
}
