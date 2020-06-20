import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;


public class MazePanel extends JPanel implements MouseListener, KeyListener, MouseMotionListener
{
    int brushWidth = 10;
    vertex[][] screen = new vertex[700/brushWidth][700/brushWidth];
    int status = 1;
    int startRow = screen[0].length/4;
    int startCol = screen.length/2;
    int endRow = screen[0].length-startRow;
    int endCol = screen.length/2;
    boolean search = false;
    int changeRow = 0;
    int changeCol = 0;
    boolean sol = false;
    int sleeptime = 1;
    LinkedList<vertex> path = new LinkedList<>();


    public MazePanel ()
    {
        super();
        for(int x=0;x<screen.length;x++)
        {
            for(int y=0;y<screen[0].length;y++)
            {
                screen[x][y] = new vertex(x,y,0);
            }
        }
        screen[startRow][startCol].setVal(2);
        screen[endRow][endCol].setVal(3);

        addMouseListener(this);
        addKeyListener(this);
        addMouseMotionListener(this);
        setSize(800,700);
    }
    public void paint(Graphics g)
    {
        if(!search)
        {
            g.setColor(Color.white);
            g.fillRect(0, 0, 700, 700);
            Color lightBrown = new Color(178, 113, 33);
            g.setColor(lightBrown);
            g.fillRect(700, 0, 100, 700);
            g.setColor(Color.black);
            g.fillRect(725, 25, 50, 50);
            g.setColor(Color.green);
            g.fillRect(725, 100, 50, 50);
            g.setColor(Color.red);
            g.fillRect(725, 175, 50, 50);
            g.setColor(Color.blue);
            g.fillRect(725, 250, 50, 50);

            g.setColor(Color.BLACK);
            for (int row = 0; row < screen.length; row++)
            {
                for (int col = 0; col < screen[0].length; col++)
                {
                    if (screen[row][col].getVal() == 1)
                        g.fillRect(row * brushWidth, col * brushWidth, brushWidth, brushWidth);
                    else if (screen[row][col].getVal() == 2)
                    {
                        g.setColor(Color.green);
                        g.fillRect(row * brushWidth, col * brushWidth, brushWidth, brushWidth);
                        g.setColor(Color.black);
                    } else if (screen[row][col].getVal() == 3)
                    {
                        g.setColor(Color.red);
                        g.fillRect(row * brushWidth, col * brushWidth, brushWidth, brushWidth);
                        g.setColor(Color.black);
                    }
                }
            }
        }
        else if(search && !sol)
        {
            g.setColor(Color.blue);
            g.fillRect(changeRow * brushWidth, changeCol * brushWidth, brushWidth, brushWidth);
        }
        else
        {
            g.setColor(Color.orange);
            g.fillRect(changeRow * brushWidth, changeCol * brushWidth, brushWidth, brushWidth);
        }
    }

    public void mouseClicked(MouseEvent e)
    {

        if(e.getX()>725 && e.getX()<775 && e.getY()>25 && e.getY()<75)
        {
            repaint();
            status = 1;
        }
        if(e.getX()>725 && e.getX()<775 && e.getY()>100 && e.getY()<150)
        {
            repaint();
            status = 2;
        }
        if(e.getX()>725 && e.getX()<775 && e.getY()>175 && e.getY()<225)
        {
            repaint();
            status = 3;
        }
        if(e.getX()>725 && e.getX()<775 && e.getY()>250 && e.getY()<300)
        {
            search = true;
            vertex bfs = BFS(screen,startRow,startCol);

            if(bfs==null)
                System.out.println("No Solution");
            else
            {
                sol=true;
                while (bfs.getParent() != null)
                {
                    try
                    {
                        Thread.sleep(sleeptime);
                    } catch (InterruptedException ex)
                    {
                        ex.printStackTrace();
                    }
                    paintImmediately(bfs.getRow() * brushWidth, bfs.getCol() * brushWidth, brushWidth, brushWidth);
                    System.out.println(bfs.getRow() + " " + bfs.getCol());
                    path.addLast(bfs);
                    bfs = bfs.getParent();
                }
                sol = false;
            }
            search = false;
        }

        int xpos = e.getX();
        xpos -= xpos % brushWidth;
        int ypos = e.getY();
        ypos -= ypos % brushWidth;
        if(status == 2 && e.getX() < 700 && e.getX() >= 0 && e.getY() >= 0 && e.getY() < 700)
        {
            screen[startRow][startCol].setVal(0);
            startRow = xpos / brushWidth;
            startCol = ypos / brushWidth;
            screen[startRow][startCol].setVal(2);
            repaint();
        }
        if(status == 3 && e.getX() < 700 && e.getX() >= 0 && e.getY() >= 0 && e.getY() < 700)
        {
            screen[endRow][endCol].setVal(0);
            endRow = xpos / brushWidth;
            endCol = ypos / brushWidth;
            screen[endRow][endCol].setVal(3);
            repaint();
        }
    }

    public void mousePressed(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void keyTyped(KeyEvent e) { }
    public void keyPressed(KeyEvent e) { }
    public void keyReleased(KeyEvent e) { }

    public void addNotify()
    {
        super.addNotify();
        requestFocus();
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        if(status==1)
        {
            int xpos = e.getX();
            xpos -= xpos % brushWidth;
            int ypos = e.getY();
            ypos -= ypos % brushWidth;

            if (e.getX() < 700 && e.getX() >= 0 && e.getY() >= 0 && e.getY() < 700)
            {
                System.out.println(e.getX() + "         " + e.getY());
                screen[xpos / brushWidth][ypos / brushWidth].setVal(1);
                repaint();
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) { }
    public static class vertex
    {
        private int row;
        private int col;
        private vertex parent;
        private int val;
        private boolean visited;
        private int direction;

        public vertex(int row, int col, int val)
        {
            this.row=row;
            this.col=col;
            this.val=val;
            parent=null;
            visited= false;
            direction = 0;
        }

        public void setVal(char val)
        {
            this.val = val;
        }

        public int getRow()
        {
            return row;
        }

        public int getVal()
        {
            return val;
        }

        public int getDirection()
        {
            return direction;
        }

        public void setDirection(int direction)
        {
            this.direction = direction;
        }

        public void setVal(int val)
        {
            this.val = val;
        }

        public void setRow(int row)
        {
            this.row = row;
        }

        public int getCol()
        {
            return col;
        }

        public void setCol(int col)
        {
            this.col = col;
        }

        public vertex getParent()
        {
            return parent;
        }

        public boolean isVisited()
        {
            return visited;
        }

        public void setVisited(boolean visited)
        {
            this.visited = visited;
        }

        public void setParent(vertex parent)
        {
            this.parent = parent;
        }
        public LinkedList getNeighbors(vertex[][] board)
        {
            LinkedList<vertex> ll = new LinkedList<>();
            if(row+1<board.length)
                ll.add(board[row+1][col]);
            if(row-1>=0)
                ll.add(board[row-1][col]);
            if(col+1<board[0].length)
                ll.add(board[row][col+1]);
            if(col-1>=0)
                ll.add(board[row][col-1]);
            return ll;
        }
    }
    public vertex BFS(vertex[][] board, int startRow, int startCol)
    {
        HashSet<String> hs = new HashSet<>();
        for(int x=0;x<board.length;x++)
        {
            for(int y=0;y<board[0].length;y++)
            {
                board[x][y].setVisited(false);
            }
        }
        LinkedList<vertex> ll = new LinkedList<>();
        ll.add(board[startRow][startCol]);

        while(!ll.isEmpty())
        {
            vertex parent = ll.removeFirst();
            parent.setVisited(true);


                hs.add(parent.row+" "+parent.col);
                if (parent.getVal() == 0 || parent.getVal() == 2)
                {
                    changeRow = parent.getRow();
                    changeCol = parent.getCol();
                    if(parent.getVal()==0)
                        paintImmediately(changeRow * brushWidth, changeCol * brushWidth, brushWidth, brushWidth);
                    try
                    {
                        Thread.sleep(sleeptime);
                    } catch (InterruptedException ex)
                    {
                        ex.printStackTrace();
                    }
                    LinkedList<vertex> ll2 = parent.getNeighbors(board);
                    while (!ll2.isEmpty())
                    {
                        vertex v = ll2.removeFirst();
                        if(!hs.contains(v.getRow()+" "+v.getCol()))
                        {
                            v.setParent(parent);
                            ll.addLast(v);
                            hs.add(v.getRow()+" "+v.getCol());
                        }
                        if(v.getVal()==3)
                            return v;
                    }
                }

            else if(parent.getVal()==3)
            {
                return parent;
            }
        }
        return null;
    }
    public vertex BDS(vertex[][] board)
    {
        HashSet<String> hs = new HashSet<>();
        for(int x=0;x<board.length;x++)
        {
            for(int y=0;y<board[0].length;y++)
            {
                board[x][y].setVisited(false);
            }
        }
        LinkedList<vertex> ll = new LinkedList<>();
        ll.add(board[startRow][startCol]);
        ll.add(board[endRow][endCol]);

        while(!ll.isEmpty())
        {
            vertex parent = ll.removeFirst();
            parent.setVisited(true);

            hs.add(parent.row+" "+parent.col);
            if (parent.getVal() == 0 || parent.getVal() == 2 || parent.getVal()==3)
            {
                changeRow = parent.getRow();
                changeCol = parent.getCol();
                if(parent.getVal()==0)
                    paintImmediately(changeRow * brushWidth, changeCol * brushWidth, brushWidth, brushWidth);
                try
                {
                    Thread.sleep(sleeptime);
                } catch (InterruptedException ex)
                {
                    ex.printStackTrace();
                }
                LinkedList<vertex> ll2 = parent.getNeighbors(board);
                while (!ll2.isEmpty())
                {
                    vertex v = ll2.removeFirst();
                    if(v.getDirection()==1 && parent.getDirection()==2)
                    {
                        //v.setParent(parent);
                        screen[endRow][endCol].setParent(v.getParent());
                        return v;
                    }
                    if(!hs.contains(v.getRow()+" "+v.getCol()))
                    {
                        if(parent.getDirection()==1)
                        {
                            v.setParent(parent);
                            v.setDirection(1);
                            ll.addLast(v);
                            hs.add(v.getRow() + " " + v.getCol());
                        }
                        else
                        {
                            v.setParent(parent);
                            v.setDirection(2);
                            ll.addLast(v);
                            hs.add(v.getRow() + " " + v.getCol());
                        }
                    }

                }
            }
        }
        return null;
    }
    public boolean DFS(vertex[][] board, vertex v, HashSet<String> hs)
    {
        v.setVisited(true);
        changeRow = v.getRow();
        changeCol = v.getCol();
        paintImmediately(changeRow * brushWidth, changeCol * brushWidth, brushWidth, brushWidth);
        hs.add(v.getRow()+" "+v.getCol());
        if(v.getVal()==3)
            return true;

        LinkedList<vertex> vertices = v.getNeighbors(board);

        for(int x=0;x<vertices.size();x++)
        {
            vertex vv = vertices.removeFirst();

            if (!vv.isVisited() && !hs.contains(vv.getRow()+" "+vv.getCol()) &&  vv.getVal() != 1)
            {
                vv.setParent(v);
                return DFS(board, vv, hs);
            }
        }

        return false;
    }
}