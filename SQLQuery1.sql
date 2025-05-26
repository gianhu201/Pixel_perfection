USE master;
GO

ALTER DATABASE Pixel_perfection SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
GO

DROP DATABASE Pixel_perfection;
GO

CREATE DATABASE Pixel_perfection;
GO

USE Pixel_perfection;
GO
-- 1. Roles
CREATE TABLE roles (
    id INT PRIMARY KEY IDENTITY(1,1),
    role_name NVARCHAR(50) NOT NULL
);

-- 2. Users
CREATE TABLE users (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100),
    user_name NVARCHAR(100) UNIQUE NOT NULL,
    password NVARCHAR(255) NOT NULL,
    email NVARCHAR(100) UNIQUE NOT NULL,
    status BIT DEFAULT 1
);

-- 3. UserRoles
CREATE TABLE user_roles (
    user_id INT,
    role_id INT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- 4. Categories
CREATE TABLE categories (
    id INT PRIMARY KEY IDENTITY(1,1),
    category_name NVARCHAR(100) NOT NULL
);

-- 5. Photos
CREATE TABLE photos (
    id INT PRIMARY KEY IDENTITY(1,1),
    url NVARCHAR(255),
    title NVARCHAR(200),
    upload_date DATETIME DEFAULT GETDATE(),
    category_id INT,
    status BIT DEFAULT 1,
    views INT DEFAULT 0,
    downloads INT DEFAULT 0,
	is_4k BIT DEFAULT 0,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- 6. Download Packages
CREATE TABLE download_packages (
    id INT PRIMARY KEY IDENTITY(1,1),
    title NVARCHAR(100),
    download_limit INT,
    price DECIMAL(10,2),
    duration_days INT,
    created_at DATETIME DEFAULT GETDATE(),
    is_4k BIT DEFAULT 0
);

-- 7. User Packages
CREATE TABLE user_packages (
    id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT,
    package_id INT,
    start_date DATETIME,
    end_date DATETIME,
    download_left INT,
    status BIT DEFAULT 1,
	is_4k BIT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (package_id) REFERENCES download_packages(id)
);

-- 8. History Downloads
CREATE TABLE history_downloads (
    id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT,
    photo_id INT,
    downloaded_at DATETIME DEFAULT GETDATE(),
    package_id INT,
    is_4k BIT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (photo_id) REFERENCES photos(id),
    FOREIGN KEY (package_id) REFERENCES download_packages(id)
);

-- 9. Transactions
CREATE TABLE transactions (
    id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT,
    amount DECIMAL(10,2),
    method NVARCHAR(50),
    status NVARCHAR(50),
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(id)
);


-- INSERT

-- Roles
INSERT INTO roles(role_name) VALUES ('ADMIN'), ('USER');

-- Users
INSERT INTO users(email, name, username, password, status)
VALUES 
('nguyena@gmail.com','Nguyen Van A',  'nguyenvana', '123456',  1),
('Tran Thi B', 'tranthib', '123456', 'tranb@com.com', 1);

-- UserRoles
INSERT INTO user_roles(user_id, role_id) VALUES (1, 1), (2, 2);

-- Categories
INSERT INTO categories(category_name)
VALUES ('Nature'), ('Anime'), ('3D'), ('Ilustration'), ('Game');

-- Photos
INSERT INTO photos(url, title, category_id)
VALUES 
('/img/nature1.jpg', 'Beautiful Nature', 1),
('/img/anime1.jpg', 'Tech World', 2),
('/img/3D1.jpg', '3D Model', 3),
('/img/Ilustration1.jpg', 'Digital Art', 4),
('/img/game1.jpg', 'Game Wallpaper', 5);

-- Download Packages
INSERT INTO download_packages(title, download_limit, price, duration_days)
VALUES 
('Basic', 50, 75000, 30), 
('Premium', 200, 250000, 30),
('Business', NULL, 630000, 30);

-- User Packages
INSERT INTO user_packages(user_id, package_id, start_date, end_date, download_left)
VALUES (2, 1, GETDATE(), DATEADD(DAY, 30, GETDATE()), 20);

-- History Downloads
INSERT INTO history_downloads(user_id, photo_id, package_id)
VALUES (2, 1, 1);

-- Transactions
INSERT INTO transactions(user_id, amount, method, status)
VALUES (2, 50000, 'Bank', 'Success');
