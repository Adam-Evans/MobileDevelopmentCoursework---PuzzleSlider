package mobile.labs.acw;

public class Cell {

    private String content;
    private int posX;
    private int posY;

    public Cell (String pContent)
    {
        content = pContent;
    }

    public Cell (String pContent, int pPosX, int pPosY)
    {
        content = pContent;
        posX = pPosX;
        posY = pPosY;
    }

    public void setXPos(int pos)
    {
        posX = pos;
    }

    public int getXPos()
    {
        return posX;
    }

    public void setYPos(int pos)
    {
        posY = pos;
    }

    public int getYPos()
    {
        return posY;
    }



}
