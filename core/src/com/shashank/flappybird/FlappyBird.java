package com.shashank.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture topTube;
	Texture bottomTube;
	Texture gameoverscreen;
	Texture []birds;
	Texture []sound;
	OrthographicCamera camera;
	//ShapeRenderer shapeRenderer;
	Circle birdCircle;

	Music gamemusic;
	Music gameover;

	int flapstate=0;
	int gamestate=0;
	float birdY=0;
	float velocity=0;
	float gravity= (float) 1.5;
	float gap=500;
	float maxTubeOffset;
	Random randomGenerator;

	float tubeVelocity=4;
	int numberOfTubes=4;

	float[] tubeOffset=new float[numberOfTubes];
	float[] tubeX=new float[numberOfTubes];
	float distanceBetweenTubes;

	Rectangle topTubeRectangles[];
	Rectangle bottomTubeRectangles[];

	int score=0;
	int scoringTube=0;
	int highScore=0;
	int soundchoice;

	BitmapFont scorefont;
	BitmapFont messagefont;



	Preferences prefs;


	@Override
	public void create () {

		prefs=Gdx.app.getPreferences("My preferences");
		batch = new SpriteBatch();
		background=new Texture("bg.png");
		gameoverscreen=new Texture("gameovers.png");
		sound=new Texture[2];
		sound[0]=new Texture("sound1.png");
		sound[1]=new Texture("sound2.png");
		soundchoice=0;
		camera=new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
	//	shapeRenderer=new ShapeRenderer();
		birdCircle=new Circle();
		gamemusic=Gdx.audio.newMusic(Gdx.files.internal("flappymusic.mp3"));
		gamemusic.setLooping(true);

		gameover=Gdx.audio.newMusic(Gdx.files.internal("gameover.mp3"));
		gameover.setLooping(false);

		topTubeRectangles=new Rectangle[numberOfTubes];
		bottomTubeRectangles=new Rectangle[numberOfTubes];

		scorefont=new BitmapFont();
		scorefont.setColor(Color.WHITE);
		scorefont.getData().scale(7);
		scorefont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		messagefont=new BitmapFont();
		messagefont.setColor(Color.BLACK);
		messagefont.getData().scale(5);
		messagefont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		birds=new Texture[2];
		birds[0]=new Texture("bird.png");
		birds[1]=new Texture("bird2.png");

		topTube=new Texture("toptube.png");
		bottomTube=new Texture("bottomtube.png");

		maxTubeOffset=Gdx.graphics.getHeight()/2-gap/2-100;

		randomGenerator=new Random();

		distanceBetweenTubes=Gdx.graphics.getWidth()*3/4;

		birdY=(Gdx.graphics.getHeight()-birds[flapstate].getHeight())/2;

		startGame();

	}

	public void startGame(){

		for(int i=0;i<numberOfTubes;i++){

			tubeX[i]=Gdx.graphics.getWidth()/2-topTube.getWidth()/2+Gdx.graphics.getWidth()*3/4+i*distanceBetweenTubes;

			tubeOffset[i]=(randomGenerator.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-gap-200);

			topTubeRectangles[i]=new Rectangle();

			bottomTubeRectangles[i]=new Rectangle();
		}

	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		/*if(Gdx.input.isTouched())
		{
			Vector3 tmp=new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
			Gdx.app.log("Tap",tmp.toString());
			camera.unproject(tmp);
			Rectangle textureBounds=new Rectangle(Gdx.graphics.getWidth()*0.03f,Gdx.graphics.getHeight()*0.88f,sound[0].getWidth(),sound[1].getHeight());
			// texture x is the x position of the texture
			// texture y is the y position of the texture
			// texturewidth is the width of the texture (you can get it with texture.getWidth() or textureRegion.getRegionWidth() if you have a texture region
			// textureheight is the height of the texture (you can get it with texture.getHeight() or textureRegion.getRegionhHeight() if you have a texture region
			if(textureBounds.contains(tmp.x,tmp.y))
			{
				// you are touching your texture
				if(soundchoice==0)
					soundchoice=1;
				else
					soundchoice=0;
				Gdx.app.log("Texture","Tapped");

			}
		}
		batch.draw(sound[soundchoice],Gdx.graphics.getWidth()*0.03f,Gdx.graphics.getHeight()*0.88f);*/

		if(gamestate==1){

			if(tubeX[scoringTube]<Gdx.graphics.getWidth()/2){
				score++;

				if(scoringTube<numberOfTubes-1){
					scoringTube++;
				}
				else
					scoringTube=0;
			}


			if(Gdx.input.justTouched()) {
				velocity=-25;
			}
			for(int i=0;i<numberOfTubes;i++) {

				if(tubeX[i]<-topTube.getWidth()){
					tubeX[i]+=numberOfTubes*distanceBetweenTubes;
					tubeOffset[i]=(randomGenerator.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-gap-200);
				}
				else{
					tubeX[i]=tubeX[i]-tubeVelocity;
				}

			//	batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				//batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

				topTubeRectangles[i]=new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i],topTube.getWidth(),topTube.getHeight());
				bottomTubeRectangles[i]=new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i],bottomTube.getWidth(),bottomTube.getHeight());
			}

			if(birdY>0&&birdY<Gdx.graphics.getHeight()){
				velocity+=gravity;
				birdY-=velocity;
			}
			else{
				gamestate=2;
				gamemusic.stop();
				gameover.play();
			}

		}
		else if(gamestate==0) {
			//messagefont.draw(batch,"Tap to start game",200,500);
			messagefont.draw(batch,"Tap to start game",Gdx.graphics.getWidth()/5,Gdx.graphics.getHeight()*0.4f);
			if(Gdx.input.justTouched()) {
				gamestate = 1;
				gamemusic.play();
			}
		}
		else if(gamestate==2){
			highScore=prefs.getInteger("highscore",00);
			if(score>highScore)
				highScore=score;
			prefs.putInteger("highscore",highScore);
			prefs.flush();

			//if(highScore==-1)
			//	highScore=score;
			if(birdY>=Gdx.graphics.getHeight())
				birdY=Gdx.graphics.getHeight()-birds[flapstate].getHeight();

			//birdY=0;
			//batch.draw(gameoverscreen,Gdx.graphics.getWidth()/2-gameoverscreen.getWidth()/2,Gdx.graphics.getHeight()/2-gameoverscreen.getHeight()/2);
			if(Gdx.input.justTouched()){
				birdY=(Gdx.graphics.getHeight()-birds[flapstate].getHeight())/2;
				gamestate=1;
				startGame();
				score=0;
				scoringTube=0;
				velocity=0;
				gameover.stop();
				gamemusic.play();
			}


		}

		if(flapstate==0)
			flapstate=1;
		else
			flapstate=0;

		for(int i=0;i<numberOfTubes;i++) {
			batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
			batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);
		}

		batch.draw(birds[flapstate],(Gdx.graphics.getWidth()-birds[flapstate].getWidth())/2,birdY);
		if(gamestate==2) {
			batch.draw(gameoverscreen, Gdx.graphics.getWidth() / 2 - gameoverscreen.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameoverscreen.getHeight() / 2);
			messagefont.draw(batch,"Tap to play again",Gdx.graphics.getWidth()/5,Gdx.graphics.getHeight()*0.4f);
			scorefont.draw(batch,"High Score:"+highScore,Gdx.graphics.getWidth()*0.05f,Gdx.graphics.getHeight()*0.2f);
		}
		scorefont.draw(batch,"Your Score:"+String.valueOf(score),Gdx.graphics.getWidth()*0.05f,Gdx.graphics.getHeight()*0.1f);

		batch.end();

		birdCircle.set(Gdx.graphics.getWidth()/2,birdY+birds[flapstate].getHeight()/2,birds[flapstate].getWidth()/2);

		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		//shapeRenderer.rect(Gdx.graphics.getWidth()*0.03f,Gdx.graphics.getHeight()*0.88f,sound[0].getWidth(),sound[1].getHeight());
		//shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);
		for(int i=0;i<numberOfTubes;i++) {
			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i],topTube.getWidth(),topTube.getHeight());
			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i],bottomTube.getWidth(),bottomTube.getHeight());

			if(Intersector.overlaps(birdCircle,topTubeRectangles[i])||Intersector.overlaps(birdCircle,bottomTubeRectangles[i])){
				//collison detected
				//Gdx.app.log("Collison","detected");
				gamestate=2;
				gamemusic.stop();
				gameover.play();


			}

		}

			//shapeRenderer.end();

	}
	
	/*@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}*/
}
