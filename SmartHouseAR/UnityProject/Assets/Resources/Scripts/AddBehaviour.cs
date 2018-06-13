using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class AddBehaviour : MonoBehaviour {

    private bool change = true;
    public Text textDisplay;
    public Text textStored;

    void Create()
    {
    }
    // Update is called once per frame
    void Update()
    {

        if ((Input.touchCount > 0) && (Input.GetTouch(0).phase == TouchPhase.Began))
        {
            Ray raycast = Camera.main.ScreenPointToRay(Input.GetTouch(0).position);
            RaycastHit raycastHit;
            if (Physics.Raycast(raycast, out raycastHit))
            {
                if (raycastHit.collider.gameObject.name == "Plus")
                {
                    int a = Int32.Parse(textStored.text) + 1;
                    textStored.text = a.ToString();
                    textDisplay.text = textStored.text + "ºC";
                }
            }
        }
    }
}
