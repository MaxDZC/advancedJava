package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	private float distanceFromPlayer = 50;
	private float angleAroundPlayer = 0;
	
	private Vector3f position = new Vector3f(100,35,50);
	private float pitch = 10;
	private float yaw;
	private float roll;
	
	private Player player;
	
	public Camera(Player player){
		this.player = player;
	}
	
	public void move(){
		float horizontalDistance;
		float verticalDistance;
		
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		
		horizontalDistance = calculateHorizontalDistance();
		verticalDistance = calculateVerticalDistance();
		
		calculateCameraPosition(horizontalDistance, verticalDistance);
		yaw = 180 - (player.getRotY() + angleAroundPlayer);
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	private void calculateCameraPosition(float horizDistance, float verticDistance) {
		float theta;
		float offsetX;
		float offsetZ;
		
		theta = player.getRotY() + angleAroundPlayer;
		offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + verticDistance;
		
		if(position.y < 0) {
			position.y = 0;
		}
	}
	
	private float calculateHorizontalDistance() {
		float hD;
		
		hD = (float) (distanceFromPlayer - 30 * Math.cos(Math.toRadians(pitch)));
		if(hD < 0){
			hD = 0;
		}
		
		return hD;
	}
	
	private float calculateVerticalDistance() {
		float vD;
		
		vD = (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
		if(vD < 0) {
			vD = 0;
		}
		
		return vD;
	}
	
	private void calculateZoom() {
		float zoomLevel;
		
		zoomLevel = Mouse.getDWheel() * 0.1f;
		distanceFromPlayer -= zoomLevel;
	}
	
	private void calculatePitch() {
		float pitchChange;
		
		if(Mouse.isButtonDown(1)) {
			pitchChange = Mouse.getDY() * 0.1f;
			pitch -= pitchChange;
			if(pitch < 0) {
				pitch = 0;
			} else if(pitch > 90) {
				pitch = 90;
			}
		}
	}
	
	private void calculateAngleAroundPlayer() {
		float angleChange;
		if(Mouse.isButtonDown(0)) {
			angleChange = Mouse.getDX() * 0.3f;
			angleAroundPlayer -= angleChange;
		}
	}
}
