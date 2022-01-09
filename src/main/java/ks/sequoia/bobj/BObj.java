package ks.sequoia.bobj;

import ks.sequoia.eobj.EObj;

import java.util.List;

public interface BObj<T extends EObj> {
    public T queryEObjById(Object id);

    public List<T> queryEObjByCondition(T input);

    public boolean addEObj(T input);

    public boolean deleteEObyById(T id);
}
