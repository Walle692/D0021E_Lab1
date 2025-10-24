package Sim;

public class BindingTableEntry {

    private NetworkAddr _hoa;
    private NetworkAddr _coa;
    private int _lifetime;

    BindingTableEntry(NetworkAddr hoa, NetworkAddr coa, int lifetime)
    {
        _hoa = hoa;
        _coa = coa;
        _lifetime = lifetime;
    }

    public NetworkAddr get_coa()
    {
        return _coa;
    }

    public NetworkAddr get_hoa()
    {
        return _hoa;
    }

    public int get_lifetime()
    {
        return _lifetime;
    }


    public void set_lifetime(int lifetime)
    {
        _lifetime = lifetime;
    }

    public  void set_coa(int coa)
    {
        _coa = coa;
    }

    public   void set_hoa(int hoa)
    {
        _hoa = hoa;
    }
}
