package OpenCraft.Interfaces;

public interface LevelRendererListener {
    void tileChanged(int var1, int var2, int var3);

    void lightColumnChanged(int var1, int var2, int var3, int var4);

    void allChanged();

}
