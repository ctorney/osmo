import cv2
import numpy as np
from math import *

mnum=17
cap = cv2.VideoCapture('DJI_00'+str(mnum)+'.MOV')

if mnum<15:
    angle=(35.7  )
else:
    angle=(24.6)
    
#angle=0.01

ret, frame = cap.read()
gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    
cv2.imwrite(str(mnum)+'PRE.jpg',gray)


alpha_=angle
beta_ = 90
gamma_=90

alpha = (alpha_ - 90.)*pi/180;
beta = (beta_ - 90.)*pi/180;
gamma = (gamma_ - 90.)*pi/180;
f = 12447
dist = 8000

sz = gray.shape
w = sz[1]
h= sz[0]

A1 = np.array([[1, 0, -w/2],[0,1, -h/2],[0, 0,0],[0, 0,1]])

RX = np.array([[1,0,0,0],[0, cos(alpha), -sin(alpha), 0],[0,sin(alpha), cos(alpha),0],[0,0,0,1]])
RY = np.eye(4)
RZ = np.eye(4)

        #Mat RY = (Mat_<double>(4, 4) << cos(beta), 0, -sin(beta), 0, 0, 1, 0, 0, sin(beta), 0,  cos(beta), 0,                0, 0,          0, 1); 

        #Mat RZ = (Mat_<double>(4, 4) << cos(gamma), -sin(gamma), 0, 0,  sin(gamma),  cos(gamma), 0, 0,                0,          0, 1, 0,0,0,0, 1); 


R = RX #* RY * RZ; 

        
T = np.array([[1, 0, 0, 0],[0, 1, 0, 0],[0,0, 1, dist],[0, 0, 0, 1]])
A2 = np.array([[f, 0, w/2,0],[0, f, h/2, 0], [0,0,1,0]])

        
M2 = np.dot(A2,np.dot(T, np.dot(R,A1)))
print(M2)
warped = cv2.warpPerspective(gray, M2, (sz[1],sz[0]),flags=cv2.WARP_INVERSE_MAP)


cv2.imwrite(str(mnum)+'POST.jpg',warped)
cap.release()

