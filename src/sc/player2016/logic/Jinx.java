package sc.player2016.logic;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sc.plugin2016.GameState;
import sc.plugin2016.Move;

public class Jinx {
	
	public static boolean jinxIsPlayingVertical;
	
	//Hopefully not necessary in final version...
	private static final Random rand = new SecureRandom();
	
	private static final int DEPTH = 4;
	
	private static boolean isFirstMove = true;
	
	private static Board board;
	
	private static Field nextMoveByJinx;
	
	//lastMoveByJinx = secondLastMove
	//Needed in board.getPossibleMoves().
	//Updated in findMove()
	private static Field lastMoveByJinx = null;
	
	enum FieldColor{
		black, green, jinx, opponent
	}
	
	/*Generates the best move, Jinx1 is able to find.
	 * 
	 * */
	public static Move findMove(GameState gameState){
//		System.out.println("*** Es wurde ein Zug angefordert");
//		System.out.println("Round = " + gameState.getRound());
//		System.out.println("Last move x = " + gameState.getLastMove().getX() + 
//				"  y = " + gameState.getLastMove().getY());
//		System.out.println("Turn = " + gameState.getTurn());
		
		System.out.println("Round " + gameState.getRound() + "  ----------------------");
		
		Move selection;
		
                //Github test change
                System.out.println("Github test");
                
		if(gameState.getTurn() == 0){
			jinxIsPlayingVertical = true;
			
			//initialize board
			board = new Board(gameState);
			selection = getFirstMoveOfGame(gameState);
		}else{
			if(gameState.getTurn() == 1){
				jinxIsPlayingVertical = false;
				
				//initialize board
				Move firstMove = gameState.getLastMove();
				board = new Board(gameState);
				board.getField(firstMove.getX(), firstMove.getY()).setFieldColor(FieldColor.opponent);
			}
			
			//update board (add move (and sometimes connection) by opponent)
			board.updateBoard(board.getField(gameState.getLastMove().getX(), gameState.getLastMove().getY()), false);
//			List<Move> possibleMoves = gameState.getPossibleMoves();
			
//			System.out.println("*** sende zug: ");
//			selection = possibleMoves.get(rand.nextInt(possibleMoves
//					.size()));
//			System.out.println("*** setze Strommast auf x="
//					+ selection.getX() + ", y="
//					+ selection.getY());
			Field nextMove = calcBestMove(board.getField(gameState.getLastMove().getX(), gameState.getLastMove().getY()), lastMoveByJinx, DEPTH);
			selection = new Move(nextMove.getX(), nextMove.getY());
		}

		//update board (add move (and sometimes connection) by jinx)
		board.updateBoard(board.getField(selection.getX(), selection.getY()), true);
				
		lastMoveByJinx = board.getField(selection.getX(), selection.getY());
		return selection;
	}
	
	
	private static boolean timeIsOver = false;
//	private static Field calcBestMoveIterative(Field lastMove, Field secondLastMove, int timeToSearch){
//		long startTime = System.currentTimeMillis();
//		ArrayList<Field> possibleMoves = board.getPossibleMoves(lastMove, secondLastMove);
//		
//		for(int depth=1; !timeIsOver; depth++){
//			possibleMoves = iterativeMax(depth, Integer.MIN_VALUE, Integer.MAX_VALUE, lastMove, secondLastMove, depth, possibleMoves)
//		}
//	}
	
	private static float iterativeMax(int depth, float alpha, float beta, Field lastMove, Field secondLastMove, int depthAtStart, ArrayList<Field> sortedMoves){
		ArrayList<Field> resortedMoves = new ArrayList<Field>();//just needed when depth==dephAtStart
		
		if(depth == 0){
			return board.evaluateBoardPosition();
		}else if(depth == depthAtStart-1){
			System.out.print(".");
		}
		
		ArrayList<Field> possibleMoves = new ArrayList<Field>();
		if(depth==depthAtStart){
			possibleMoves = sortedMoves;
		}else{
			possibleMoves = board.getPossibleMoves(lastMove, secondLastMove);
		}
		
		float maxValue = alpha; //minimum that jinx can reach (found in previous nodes)
		for(Field move : possibleMoves){
			board.updateBoard(move, true);
			float value = min(depth-1, maxValue, beta, move, lastMove, depthAtStart);
			board.undoMove(move, true);
			if(value > maxValue){
				maxValue = value;
				if(maxValue >= beta)//opponent would never allow this move (previous combination
					break;			// searched, that had a better (lower) result for opponent)
					
				if(depth == depthAtStart){
					nextMoveByJinx = move;
				}
			}
			if(depth==depthAtStart){
				//resort the possibleMoves at start
				
			}
		}
		return maxValue;
	}
	
//	private static void insertMoveIn(ArrayList<Field><int> sortedMoves, Field move, int value))
	
	//needs last Move of the opponent. Calculates next move reacting to olastMove.
	private static Field calcBestMove(Field lastMove, Field secondLastMove, int depth){
		long startTime = System.currentTimeMillis();
		float evaluation = max(depth, -100000, 100000, lastMove, secondLastMove, depth);
		System.out.println("Time = " + (System.currentTimeMillis() - startTime)/(float)1000 + " ");
		return nextMoveByJinx;
		
	}
	
	private static float max(int depth, float alpha, float beta, Field lastMove, Field secondLastMove, int depthAtStart){
//		System.out.println("max mit tiefe = " + depth);
		if(depth == 0){
			return board.evaluateBoardPosition();
		}else if(depth == depthAtStart-1){
			System.out.print(".");
		}
		ArrayList<Field> possibleMoves = board.getPossibleMoves(lastMove, secondLastMove);
		float maxValue = alpha; //minimum that jinx can reach (found in previous nodes)
		for(Field move : possibleMoves){
			board.updateBoard(move, true);
			float value = min(depth-1, maxValue, beta, move, lastMove, depthAtStart);
			board.undoMove(move, true);
			if(value > maxValue){
				maxValue = value;
				if(maxValue >= beta)//opponent would never allow this move (previous combination
					break;			// searched, that had a better (lower) result for opponent)
					
				if(depth == depthAtStart){
					nextMoveByJinx = move;
				}
			}
		}
		return maxValue;
	}
	
	private static float min(int depth, float alpha, float beta, Field lastMove, Field secondLastMove, int depthAtStart){
		
//		System.out.println("min mit tiefe = " + depth);
		if(depth == 0){
			return board.evaluateBoardPosition();
		}else if(depth == depthAtStart-1){
			System.out.print(".");
		}
		ArrayList<Field> possibleMoves = board.getPossibleMoves(lastMove, secondLastMove);
		float minValue = beta; //maximum that beta can reach (found in previous nodes)
		for(Field move : possibleMoves){
			board.updateBoard(move, false);
			float value = max(depth-1,alpha, minValue, move, lastMove, depthAtStart);
			board.undoMove(move, false);
			if(value < minValue){
				minValue = value;
				if(minValue <= alpha)//jinx would never allow this move (previous combination
					break;			// searched, that had a better (higher) result for jinx)
			}
		}
		return minValue;
	}
	
	//Gets first move of the game (is just needed, when Jinx begins) depending on 
	//the positions of the green fields (maxNumberOfGreenFields = 3x3 + 2x2 + 2x2 + 1 = 18)
	private static Move getFirstMoveOfGame(GameState gameState){
		Move result = null;
		
		//in jinx 0.01 the first move will be on a field (x,y) with 10 <= x <= 14
		// and 10 <= y <= 14 .
		for(int row = 10; row<=14 && result == null; row++){
			for(int col=10; col<=14 && result == null; col++){
				if(board.getField(row, col).getFieldColor() == FieldColor.black)
					result = new Move(row, col);
			}
		}
		
		return result;
	}
	
	private static void printPossibleMoves(List<Move> possibleMoves){
		System.out.println("Possible Moves:");
		int i=1;
		for(Move move : possibleMoves){
			System.out.println(i + ": " + "x=" + move.getX() + ", y=" + move.getY());
			i++;
		}
		System.out.println();
	}
	
}




