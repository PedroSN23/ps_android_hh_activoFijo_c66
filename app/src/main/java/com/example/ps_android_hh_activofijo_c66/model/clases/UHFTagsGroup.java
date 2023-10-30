package com.example.ps_android_hh_activofijo_c66.model.clases;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.pp_android_handheld_library.controller.rfid.UHFTagsRead;
import com.example.pp_android_handheld_library.model.TagsTipo;

import java.util.ArrayList;

public class UHFTagsGroup implements Parcelable {
    private final ArrayList<UHFTagsRead> uhfTagsReads;

    public UHFTagsGroup(UHFTagsRead uhfTagsRead) {
        this.uhfTagsReads = new ArrayList<>();
        uhfTagsReads.add(uhfTagsRead);
    }

    public UHFTagsGroup(Parcel in) {
        this.uhfTagsReads = new ArrayList<>();
        in.readParcelableList(uhfTagsReads, UHFTagsRead.class.getClassLoader());
    }

    public static final Creator<UHFTagsGroup> CREATOR = new Creator<UHFTagsGroup>() {
        @Override
        public UHFTagsGroup createFromParcel(Parcel in) {
            return new UHFTagsGroup(in);
        }

        @Override
        public UHFTagsGroup[] newArray(int size) {
            return new UHFTagsGroup[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelableList(uhfTagsReads, PARCELABLE_WRITE_RETURN_VALUE);
    }

    public TagsTipo getTagsTipo() {
        return uhfTagsReads.get(0).getTipo();
    }

    public String getDetail() {
        return uhfTagsReads.get(0).getDetail();
    }

    public String getEpcAt(int i) {
        return uhfTagsReads.get(i).getEpc();
    }

    public void addTagFile(UHFTagsRead uhfTagsRead) {
        for(UHFTagsRead tr: uhfTagsReads) {
            if(tr.getEpc().compareTo(uhfTagsRead.getEpc())==0) {
                return;
            }
        }
        uhfTagsReads.add(uhfTagsRead);
    }

    public int getLeidos() {
        int ret = 0;
        for(UHFTagsRead tr: uhfTagsReads) {
            if(tr.getInventariado()==-1 || tr.getInventariado()==1) {
                ret++;
            }
        }
        return ret;
    }

    public int getEsperados() {
        int ret = 0;
        for(UHFTagsRead tr: uhfTagsReads) {
            if(tr.getInventariado()==0 || tr.getInventariado()==1) {
                ret++;
            }
        }
        return ret;
    }

    public int checkStatus() {
        int inv = 0;
        for(UHFTagsRead tr: uhfTagsReads) {
            if(tr.getInventariado()==-1) {
                return -1;
            } else {
                if(tr.getInventariado()==1) {
                    inv++;
                }
            }
        }
        if(inv==uhfTagsReads.size()) {
            return 1;
        }
        return 0;
    }

    public boolean addLectura(UHFTagsRead uhfTagsRead, boolean filterTags) {
        for(UHFTagsRead tr: uhfTagsReads) {
            if(tr.getEpc().compareTo(uhfTagsRead.getEpc())==0) {
                if(tr.getInventariado()==0) {
                    tr.setInventariado(1);
                    return true;
                }
                return false;
            }
        }
        if(!filterTags) {
            uhfTagsRead.setInventariado(-1);
            uhfTagsReads.add(uhfTagsRead);
            return true;
        }
        return false;
    }

    public int getInventariadoAt(int i) {
        return uhfTagsReads.get(i).getInventariado();
    }

    public void setInventariadoAt(int i, int i1) {
        uhfTagsReads.get(i).setInventariado(i1);
    }

    public int getCount(int tipo) {
        int ret=0;
        for(UHFTagsRead tr: uhfTagsReads) {
            if(tr.getInventariado()==tipo) {
                ret++;
            }
        }
        return ret;
    }

    public int borrarLeidosSobrantes() {
        for(int i=uhfTagsReads.size()-1; i>=0; i--) {
            if(uhfTagsReads.get(i).getInventariado()==-1) {
                uhfTagsReads.remove(i);
            } else {
                uhfTagsReads.get(i).setInventariado(0);
            }
        }
        return uhfTagsReads.size();
    }

    public int borrarSobrantes() {
        for(int i=uhfTagsReads.size()-1; i>=0; i--) {
            if(uhfTagsReads.get(i).getInventariado()==-1) {
                uhfTagsReads.remove(i);
            }
        }
        return uhfTagsReads.size();
    }

    public int getCantidadAt(int i) {
        return uhfTagsReads.get(i).getCantidad();
    }

    public String getRssiAt(int i) {
        return uhfTagsReads.get(i).getRssi();
    }

    public int getSize() {
        return uhfTagsReads.size();
    }

    public ArrayList<UHFTagsRead> getList() {
        return uhfTagsReads;
    }

    public int compareTo(UHFTagsGroup t2, int id) { //0 = ok, 1, falt 2 sobr
        int ret=0;
        switch (checkStatus()) {
            case -1:
                switch (t2.checkStatus()) {
                    case -1:
                        ret = compareEquals(t2);
                        break;
                    case 0:
                        if(id==2) {
                            ret = -1;
                        } else {
                            ret = 1;
                        }
                        break;
                    case 1:
                        if(id==0) {
                            ret = 1;
                        } else {
                            ret = -1;
                        }
                        break;
                }
                break;
            case 0:
                switch (t2.checkStatus()) {
                    case -1:
                        if(id==2) {
                            ret = 1;
                        } else {
                            ret = -1;
                        }
                        break;
                    case 1:
                        if(id==1) {
                            ret = -1;
                        } else {
                            ret = 1;
                        }
                        break;
                    case 0:
                        ret = compareEquals(t2);
                        break;
                }
                break;
            case 1:
                switch (t2.checkStatus()) {
                    case -1:
                    case 0:
                        if(id==0) {
                            ret = -1;
                        } else {
                            ret = 1;
                        }
                        break;
                    case 1:
                        ret = compareEquals(t2);
                        break;
                }
                break;
        }
        return ret;
    }

    private int compareEquals(UHFTagsGroup t2) {
        int dif = getTagsTipo().getIndex()-t2.getTagsTipo().getIndex();
        if(dif==0) {
            if(getDetail().isEmpty()) {
                return getEpcAt(0).compareTo(t2.getEpcAt(0));
            } else {
                return getDetail().compareTo(t2.getDetail());
            }
        }
        return dif;
    }
}
