package rabbitmqtest;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class User {
	//������Ϣ�߳�
	class SendMessage implements Runnable{

		public void run() {
			while(true){
			sentMessage=sc.next();
			sentMessage=name+":"+sentMessage;
			try {
				
				channel.basicPublish(EXCHANGE_NAME, "", null, sentMessage.getBytes());
				if(sentMessage.equals(name+":exit")){
					return;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}}
			
		
	}
	//������Ϣ�߳�
	class ReceiveMessages implements Runnable{

		public void run() {
			try {
				channel.queueDeclare(name, false, false, false, null);
				channel.queueBind(name, EXCHANGE_NAME,"");
				consumer = new QueueingConsumer(channel);
				channel.basicConsume(name, true, consumer);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			while(true){
				try {
					delivery = consumer.nextDelivery();
					receiveMessage = new String(delivery.getBody());
					if(receiveMessage.equals(name+":exit")){
					System.out.println("�˳��ɹ���");
					try {
						channel.queueDelete(name);
						channel.close();
						connection.close();
						return;
						} catch (IOException e) {
						// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (TimeoutException e) {
						// TODO Auto-generated catch block
							e.printStackTrace();
								}
							}
						} catch (ShutdownSignalException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ConsumerCancelledException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println(receiveMessage);
					}
	}
		}
	private final static String EXCHANGE_NAME = "fanout_exechange";
	private static String name=null;
	private static Channel channel;
	private static QueueingConsumer consumer=null;
	private static Scanner sc=null;
	private static QueueingConsumer.Delivery delivery=null;
	private static String sentMessage=null;
	private static String receiveMessage=null;
	private static Connection connection=null;
	public static void main(String[] args) throws IOException, TimeoutException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		//�½�һ�����ӹ���
		ConnectionFactory factory=new ConnectionFactory();
		//��������RabbitMQ�ĸ������
		factory.setHost("127.0.0.1");
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setPort(5672);
		//�½�һ������
		connection=factory.newConnection();
		//�½�һ��ͨ��
		channel=connection.createChannel();
		//����һ��fanout���͵Ľ�����
		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		System.out.println("���������������");
		sc=new Scanner(System.in);
		name=sc.next();
		System.out.println("��������Ϣ����(����exit�˳�)");
		//�����߳�
		User.ReceiveMessages receive=new User().new ReceiveMessages();
		User.SendMessage send=new User().new SendMessage();
		new Thread(receive,name).start();
		new Thread(send,name).start();
		}
		


	}

