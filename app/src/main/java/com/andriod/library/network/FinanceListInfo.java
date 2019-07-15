package com.andriod.library.network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by MaYongHui on 2017/11/8.
 */

public class FinanceListInfo implements Serializable {

    @SerializedName("pageInfo")
    public PageInfoBean pageInfo;
    @SerializedName("list")
    public ArrayList<ListBean> list;

    public static class PageInfoBean implements Serializable {
        @SerializedName("pageIndex")
        public int pageIndex;
        @SerializedName("pageSize")
        public int pageSize;
        @SerializedName("total")
        public int total;
    }

    public static class ListBean {
        @SerializedName("isRate")
        public String isRate;
        @SerializedName("termMonth")
        public int termMonth;
        @SerializedName("marketingLabel")
        public String marketingLabel;
        @SerializedName("isTransfer")
        public String isTransfer;
        @SerializedName("addRate")
        public String addRate;
        @SerializedName("isCashback")
        public String isCashback;
        @SerializedName("repayMethodName")
        public String repayMethodName;
        @SerializedName("loanTitle")
        public String loanTitle;
        @SerializedName("isPause")
        public String isPause;
        @SerializedName("baseRate")
        public String baseRate;
        @SerializedName("loanShare")
        public double loanShare;
        @SerializedName("term")
        public int term;
        @SerializedName("id")
        public String id;
        @SerializedName("termName")
        public String termName;
        @SerializedName("availShare")
        public double availShare;
        @SerializedName("status")
        public String status;
        @SerializedName("schedule")
        public String schedule;
        @SerializedName("transferShare")
        public double transferShare;
        @SerializedName("transferMoney")
        public double transferMoney;
        @SerializedName("baoBiaoActivityStr")
        public String baoBiaoActivityStr; //散标包标 "包标有奖"

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ListBean listBean = (ListBean) o;

            if (termMonth != listBean.termMonth) return false;
            if (Double.compare(listBean.loanShare, loanShare) != 0) return false;
            if (term != listBean.term) return false;
            if (Double.compare(listBean.availShare, availShare) != 0) return false;
            if (Double.compare(listBean.transferShare, transferShare) != 0) return false;
            if (Double.compare(listBean.transferMoney, transferMoney) != 0) return false;
            if (isRate != null ? !isRate.equals(listBean.isRate) : listBean.isRate != null)
                return false;
            if (marketingLabel != null ? !marketingLabel.equals(listBean.marketingLabel) : listBean.marketingLabel != null)
                return false;
            if (isTransfer != null ? !isTransfer.equals(listBean.isTransfer) : listBean.isTransfer != null)
                return false;
            if (addRate != null ? !addRate.equals(listBean.addRate) : listBean.addRate != null)
                return false;
            if (isCashback != null ? !isCashback.equals(listBean.isCashback) : listBean.isCashback != null)
                return false;
            if (repayMethodName != null ? !repayMethodName.equals(listBean.repayMethodName) : listBean.repayMethodName != null)
                return false;
            if (loanTitle != null ? !loanTitle.equals(listBean.loanTitle) : listBean.loanTitle != null)
                return false;
            if (isPause != null ? !isPause.equals(listBean.isPause) : listBean.isPause != null)
                return false;
            if (baseRate != null ? !baseRate.equals(listBean.baseRate) : listBean.baseRate != null)
                return false;
            if (id != null ? !id.equals(listBean.id) : listBean.id != null) return false;
            if (termName != null ? !termName.equals(listBean.termName) : listBean.termName != null)
                return false;
            if (status != null ? !status.equals(listBean.status) : listBean.status != null)
                return false;
            if (schedule != null ? !schedule.equals(listBean.schedule) : listBean.schedule != null)
                return false;
            return baoBiaoActivityStr != null ? baoBiaoActivityStr.equals(listBean.baoBiaoActivityStr) : listBean.baoBiaoActivityStr == null;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = isRate != null ? isRate.hashCode() : 0;
            result = 31 * result + termMonth;
            result = 31 * result + (marketingLabel != null ? marketingLabel.hashCode() : 0);
            result = 31 * result + (isTransfer != null ? isTransfer.hashCode() : 0);
            result = 31 * result + (addRate != null ? addRate.hashCode() : 0);
            result = 31 * result + (isCashback != null ? isCashback.hashCode() : 0);
            result = 31 * result + (repayMethodName != null ? repayMethodName.hashCode() : 0);
            result = 31 * result + (loanTitle != null ? loanTitle.hashCode() : 0);
            result = 31 * result + (isPause != null ? isPause.hashCode() : 0);
            result = 31 * result + (baseRate != null ? baseRate.hashCode() : 0);
            temp = Double.doubleToLongBits(loanShare);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            result = 31 * result + term;
            result = 31 * result + (id != null ? id.hashCode() : 0);
            result = 31 * result + (termName != null ? termName.hashCode() : 0);
            temp = Double.doubleToLongBits(availShare);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            result = 31 * result + (status != null ? status.hashCode() : 0);
            result = 31 * result + (schedule != null ? schedule.hashCode() : 0);
            temp = Double.doubleToLongBits(transferShare);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(transferMoney);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            result = 31 * result + (baoBiaoActivityStr != null ? baoBiaoActivityStr.hashCode() : 0);
            return result;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FinanceListInfo that = (FinanceListInfo) o;

        if (pageInfo != null ? !pageInfo.equals(that.pageInfo) : that.pageInfo != null)
            return false;
        return list != null ? list.equals(that.list) : that.list == null;
    }

    @Override
    public int hashCode() {
        int result = pageInfo != null ? pageInfo.hashCode() : 0;
        result = 31 * result + (list != null ? list.hashCode() : 0);
        return result;
    }
}
