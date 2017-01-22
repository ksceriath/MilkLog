package com.kscerion.milklog.util;

import android.database.Cursor;
import android.database.CursorWrapper;

public class FilterCursorWrapper extends CursorWrapper {
    private String filter;
    private int[] filterMap;
    private int mPos = -1;
    private int mCount = 0;

    public FilterCursorWrapper(Cursor cursor,String filter) {
        super(cursor);
        this.filter = filter.toLowerCase();
        int count = super.getCount();

        if (!this.filter.equals("")) {
            this.filterMap = new int[count];
            int filteredCount = 0;
            for (int i=0;i<count;i++) {
                super.moveToPosition(i);
                int columns = this.getColumnCount();
                for(int column = 1; column<columns; column++) {
                    String colValue = this.getString(column);
                    if (colValue!=null && colValue.toLowerCase().contains(this.filter)){
                        this.filterMap[filteredCount] = i;
                        filteredCount++;
                        break;
                    }
                }
            }
            this.mCount = filteredCount;
        } else {
            this.filterMap = new int[count];
            this.mCount = count;
            for (int i=0;i<count;i++) {
                this.filterMap[i] = i;
            }
        }

        this.moveToFirst();
    }

    public int getCount() { return this.mCount; }

    @Override
    public boolean moveToPosition(int position) {
        // Make sure position isn't past the end of the cursor
        final int count = getCount();
        if (position >= count) {
            mPos = count;
            return false;
        }
        // Make sure position isn't before the beginning of the cursor
        if (position < 0) {
            mPos = -1;
            return false;
        }
        final int realPosition = filterMap[position];
        // When moving to an empty position, just pretend we did it
        boolean moved = realPosition == -1 ? true : super.moveToPosition(realPosition);
        if (moved) {
            mPos = position;
        } else {
            mPos = -1;
        }
        return moved;
    }
    @Override
    public final boolean move(int offset) {
        return moveToPosition(mPos + offset);
    }
    @Override
    public final boolean moveToFirst() {
        return moveToPosition(0);
    }
    @Override
    public final boolean moveToLast() {
        return moveToPosition(getCount() - 1);
    }
    @Override
    public final boolean moveToNext() {
        return moveToPosition(mPos + 1);
    }
    @Override
    public final boolean moveToPrevious() {
        return moveToPosition(mPos - 1);
    }
    @Override
    public final boolean isFirst() {
        return mPos == 0 && getCount() != 0;
    }
    @Override
    public final boolean isLast() {
        int cnt = getCount();
        return mPos == (cnt - 1) && cnt != 0;
    }
    @Override
    public final boolean isBeforeFirst() {
        if (getCount() == 0) {
            return true;
        }
        return mPos == -1;
    }
    @Override
    public final boolean isAfterLast() {
        if (getCount() == 0) {
            return true;
        }
        return mPos == getCount();
    }
    @Override
    public int getPosition() {
        return mPos;
    }
}