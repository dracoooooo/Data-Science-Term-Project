
/**
 * Created by Shifang on 2017/10/21.
 * 游戏类，请补全代码，如有必要，你可以添加一些方法
 */

public class Game {
    private Player player_X;
    private Player player_Y;
    private Map map;

    /**
     *
     * @param initInfo 传入初始化参数,格式为:
     *                 M;x1,y1,hp1;x2,y2,hp2
     *                 分别表示
     *                 M : 棋盘宽度和高度(棋盘为方形)
     *                 x1,y1,hp1 : X 的初始位置,血量
     *                 x2,y2,hp2 : Y 的初始位置,血量
     */
    public Game(String initInfo){
        String[] infoArray = initInfo.split(";|,");
        int n = Integer.parseInt(infoArray[0]);
        int x1 = Integer.parseInt(infoArray[1]);
        int y1 = Integer.parseInt(infoArray[2]);
        int x2 = Integer.parseInt(infoArray[4]);
        int y2 = Integer.parseInt(infoArray[5]);
//        boolean isHide1;
//        boolean isHide2;
//        if(infoArray[3].equals("1")) isHide1 = true;
//        else isHide1 = false;
//        if(infoArray[6].equals("1")) isHide2 = true;
//        else isHide2 = false;
        player_X = new Player(x1,y1,Integer.parseInt(infoArray[3]),"X");
        player_Y = new Player(x2,y2,Integer.parseInt(infoArray[6]),"Y");
        map=new Map(n,player_X,player_Y);

    }

    /**
     *
     * @param steps 传入两方依次走的步骤, X 先走, 两个玩家之间用","分开,
     *              每个玩家有 3 个参数,分别为'方向','是否攻击','是否隐身'
     *              方向用 U,D,L,R 分别表示上,下,左,右
     *              是否攻击,用 1 表示攻击,用0表示不攻击
     *              是否隐身,用 1 表示隐身,用0表示不隐身
     *              输入示例：
     *                U01,L10
     *              表示 X 向上走 1 步,不攻击,隐身; Y 向左走 1 步,攻击,不隐身
     *              在游戏过程中，如果有任意一方玩家角色死亡（生命值为 0 ），则对手胜利，比如若 X 生命值被减为 0 ，则输出 Y_WIN
     *              若走完所有的步骤,双方都没有死亡,则输出 DRAW
     * @return 游戏结果
     */
    public Result playGame(String steps){
        map.print();
        String list[]=steps.split(",");
        for(int i=0;i<list.length;i++){
            if(i%2==0){ //x
                player_X.move(list[i].trim().charAt(0));
                if(list[i].trim().charAt(2)=='0') player_X.setHide(false);
                else player_X.setHide(true);
                if(list[i].trim().charAt(1)=='1'){
                    if(player_X.calDistance(player_Y)<=player_X.getGun().getRange()){
                        player_Y.setHealthPoint(player_Y.getHealthPoint()-player_Y.getGun().getDamage());
                    }
                }
            }
            else{//y
                player_Y.move(list[i].trim().charAt(0));
                if(list[i].trim().charAt(2)=='0') player_Y.setHide(false);
                else player_Y.setHide(true);
                if(list[i].trim().charAt(1)=='1'){
                    if(player_Y.calDistance(player_X)<=player_Y.getGun().getRange()){
                        player_X.setHealthPoint(player_X.getHealthPoint()-player_X.getGun().getDamage());
                    }
                }
            }
            Result r = map.move();
            if(r==Result.X_WIN) return Result.X_WIN;
            else if(r==Result.Y_WIN) return Result.Y_WIN;
            else if(i==list.length-1&&r==Result.DRAW) return Result.DRAW;
        }
        return null;
    }
    public static void main(String[] args){
        Game g = new Game("2;0,0,1;0,1,1");
        Result result=g.playGame("R10,L00,U11");
        System.out.println(result);
    }
}
