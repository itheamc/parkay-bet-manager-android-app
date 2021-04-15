package com.itheamc.parlaymanager.callbacks;

public interface ItemsClickListener {
    void onClick(int position);
    void onLongClick(int position);
    void onOptionMenuClick(int position);


    /*______________For Selection View______________*/
    void onMenuClick(int position, int type);

    /*_________Item Swipe______________*/
    void onSwipe(int position);
}
