package Sim;

public class BindingTableEntry {

    private NetworkAddr _hoa;
    private NetworkAddr _coa;

    BindingTableEntry(NetworkAddr hoa, NetworkAddr coa)
    {
        _hoa = hoa;
        _coa = coa;

    }

    public NetworkAddr get_coa()
    {
        return _coa;
    }

    public NetworkAddr get_hoa()
    {
        return _hoa;
    }


    public  void set_coa(NetworkAddr coa)
    {
        _coa = coa;
    }

    public   void set_hoa(NetworkAddr hoa)
    {
        _hoa = hoa;
    }
}
