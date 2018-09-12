package com.name.rmedal.modelbean;

import com.chad.library.adapter.base.entity.SectionEntity;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class PersonalSection extends SectionEntity<PersonalModelBean> {
    private boolean isMore;
    public PersonalSection(boolean isHeader, String header, boolean isMroe) {
        super(isHeader, header);
        this.isMore = isMroe;
    }

    public PersonalSection(PersonalModelBean t) {
        super(t);
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean mroe) {
        isMore = mroe;
    }
}
