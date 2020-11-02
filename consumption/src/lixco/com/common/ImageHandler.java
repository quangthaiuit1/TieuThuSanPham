package lixco.com.common;


import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.model.UploadedFile;



public class ImageHandler{

	private static ImageHandler instance;
	private static final Lock createLock_ = new ReentrantLock();

	private ImageHandler() {

	}
	public static ImageHandler getInstance() {
		if (instance == null) {
			createLock_.lock();
			try {
				if (instance == null) {
					instance = new ImageHandler();
				}
			} finally {
				createLock_.unlock();
			}

		}
		return instance;
	}

	public String readImageForDisk(String urlFile) {
		String encodedfile = null;
		try {
			File f = new File(urlFile);
			if(f.exists()){
			String type=FilenameUtils.getExtension(urlFile);
			InputStream fileInputStreamReader = new FileInputStream(f);
			byte[] bytes = new byte[(int) f.length()];
			fileInputStreamReader.read(bytes);
			encodedfile = new String(Base64.encodeBase64(bytes), "UTF-8");
			encodedfile="data:image/"+type+";base64, "+encodedfile;
			fileInputStreamReader.close();
			}
		}catch(FileNotFoundException ex){
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encodedfile;
	}
	public byte[] convertToImg(String base64) throws IOException {
		return Base64.decodeBase64(base64);
	}

	public void writeByteToImageFile(byte[] imgBytes,String extension, String imgFileName) throws IOException {
		File imgFile = new File(imgFileName);
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(imgBytes));
		ImageIO.write(img, extension, imgFile);
		ImageIO.setUseCache(false);
	}
	public BufferedImage thundNail(String pathImg,int width,int height){
		InputStream fileInputStreamReader;
		try{
			File f = new File(pathImg);
			if(f.exists()){
				fileInputStreamReader = new FileInputStream(f);
				byte[] bytes = new byte[(int) f.length()];
				fileInputStreamReader.read(bytes);
				
				BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
				BufferedImage scaledImg=Thumbnails.of(img)
		        .size(width, height)
		        .outputQuality(1.0)
		        .crop(Positions.TOP_CENTER)
		        .asBufferedImage();
//				BufferedImage scaledImg1 =Scalr.resize(scaledImg, Method.ULTRA_QUALITY,
//						Mode.FIT_EXACT
//						, width, height, Scalr.OP_ANTIALIAS);
				fileInputStreamReader.close();
				return scaledImg;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	public String  writeByteToImageFileThumnail(String base64, String pathSaveImage,int width,int height){
		String nameImage="";
		try {
			String[] arrStr = base64.split("base64,");
			String type = arrStr[0].split("/")[1].replace(";", "");
			byte[] imgBytes = convertToImg(arrStr[1]);
			nameImage = generateUniqueFileNameThumb(type,pathSaveImage) + "." + type;
			File imgFileThumNail=new File(pathSaveImage+"/"+nameImage);
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(imgBytes));
			BufferedImage scaledImg=Thumbnails.of(img)
			        .size(width, height)
			        .outputQuality(1.0)
			        .crop(Positions.CENTER)
			        .asBufferedImage();
//			BufferedImage scaledImg =Scalr.resize(img, Method.QUALITY,
//					Mode.FIT_EXACT
//					, width, height, Scalr.OP_ANTIALIAS);
			ImageIO.write(scaledImg, type, imgFileThumNail);
			ImageIO.setUseCache(false);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nameImage;
	}
	public String writeByteToImageFile(UploadedFile part, String pathSaveImage){
		String nameImage="";
		try{
			String nameReal=part.getFileName();
			int index = nameReal.lastIndexOf(".");
			String type = nameReal.substring(index+1);
			nameImage = generateUniqueFileName(type,pathSaveImage) + "." + type;
			File imgFile = new File(pathSaveImage+"/"+nameImage);
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(part.getContents()));
			ImageIO.write(img, type, imgFile);
			ImageIO.setUseCache(false);
		}catch(Exception e){
			e.printStackTrace();
		}
		return nameImage;
	}
	public String writeByteToImageFile(String base64, String pathSaveImage) {
		String nameImage="";
		try {
			String[] arrStr = base64.split("base64,");
			String type = arrStr[0].split("/")[1].replace(";", "");
			byte[] imgBytes = convertToImg(arrStr[1]);
			nameImage = generateUniqueFileName(type,pathSaveImage) + "." + type;
			File imgFile = new File(pathSaveImage+"/"+nameImage);
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(imgBytes));
			ImageIO.write(img, type, imgFile);
			ImageIO.setUseCache(false);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nameImage;
	}

	public String generateUniqueFileName(String type,String pathSaveImage) {
		
		int i = 0;
		String filename = Integer.toString(i);
		try{
	    createDiretory(pathSaveImage);
		File f = new File(pathSaveImage + "/" + filename + "." + type);
		while (f.exists()) {
			i++;
			filename = Integer.toString(i);
			f =  new File(pathSaveImage + "/" + filename + "." + type);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return filename;
	}
	public String generateUniqueFileNameThumb(String type,String pathSaveImage) {
		
		int i = 0;
		String filename = "thumb_"+Integer.toString(i);
		try{
			createDiretory(pathSaveImage);
			File f = new File(pathSaveImage + "/" + filename + "." + type);
			while (f.exists()) {
				i++;
				filename ="thumb_"+Integer.toString(i);
				f =  new File(pathSaveImage + "/" + filename + "." + type);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return filename;
	}
	public void deleteImage(String pathImg){
		try{
			File f=new File(pathImg);
			if(f.exists()){
			f.delete();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void createDiretory(String fileName) {
		try {
			File dir1 = new File(fileName);
			if(!dir1.exists()){
				dir1.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public  String combImage(List<String> listImg){
		try{
			int w=0;//max
			int h=0;
			List<BufferedImage> list=new ArrayList<BufferedImage>();
			for(String img:listImg){
				BufferedImage i=ImageIO.read(new File(img));
				int width=i.getWidth();
				int height=i.getHeight();
				h+=height;
				if(w<width){
					w=width;
				}
				list.add(i);
			}
			BufferedImage combin=new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics g = combin.getGraphics();
			// paint both images, preserving the alpha channels
			int heightCurr=0;
			for(BufferedImage f:list){
				g.drawImage(f,0,heightCurr,null);
				heightCurr+=f.getHeight();
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageIO.write(combin,"png",bos);
	        byte[] imageBytes = bos.toByteArray();
	       String encodedfile = new String(Base64.encodeBase64(imageBytes), "UTF-8");
			encodedfile="data:image/"+"png"+";base64, "+encodedfile;
			bos.close();
			g.dispose();
			ImageIO.setUseCache(false);
			return encodedfile;
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
