package Sim;

public class BindingTableEntry {

    private SimEnt _node;
    private int _hoa;
    private int _coa;
    private int _lifetime;

    BindingTableEntry(SimEnt node, int hoa, int coa, int lifetime)
    {
        _node = node;
        _hoa = hoa;
        _coa = coa;
        _lifetime = lifetime;
    }

    protected int get_coa()
    {
        return _coa;
    }

    protected int get_hoa()
    {
        return _hoa;
    }

    protected int get_lifetime()
    {
        return _lifetime;
    }

    protected SimEnt node()
    {
        return _node;
    }

    protected void set_lifetime(int lifetime)
    {
        _lifetime = lifetime;
    }

    protected  void set_coa(int coa)
    {
        _coa = coa;
    }

    protected   void set_hoa(int hoa)
    {
        _hoa = hoa;
    }
}
