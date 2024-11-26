-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: vehicle_rental_system
-- ------------------------------------------------------
-- Server version	8.0.37

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `Customer_id` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(20) NOT NULL,
  `Address` varchar(100) DEFAULT NULL,
  `Lisence_no` varchar(15) NOT NULL,
  `DOB` date DEFAULT NULL,
  `Phone_no` bigint DEFAULT NULL,
  `Email_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`Customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1015 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (111,'Suraj B V','NBH hostel, Tumkur','34567aa','2003-10-07',8431348081,'surajbv5566@gmail.com'),(112,'Prem kumar R','Kunigal','325362d','2003-03-15',234624522,'premkumarrnaik@gmail.com'),(115,'Gokul S P','Mysore','346246dd','2004-10-01',825256255,'gokulsp@gmail.com'),(123,'Chandan M','Tarikere','23543356','2002-01-19',9275972537,'chandanm@gmail.com'),(131,'Sharath','Kampli','2423452','2004-12-12',1212234243,'kalyanisharath@gmail.com'),(1000,'Vasu M S','Kunigal','12314345','2004-10-10',7658392022,'vasums@gmail.com'),(1001,'Vinay A','Changiri','32456544','2004-08-02',6363908512,'vinayachar@gmail.com'),(1005,'Rohith M G','Madhugiri','237245637','2004-10-01',7894561233,'rohithmg@gmail.com'),(1006,'S Abhiram','Chitradurga','86654345','2004-04-25',5478963210,'abhirams@gmail.com'),(1014,'Darshan H G','Kunigal','78945624','2004-10-18',794587412888888888,'darshanhg@gmail.com');
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-11-26 21:16:53
