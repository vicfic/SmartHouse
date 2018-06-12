using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class clickBehaviour : MonoBehaviour {

    public Color color1;
    public Color color2;
    private bool change = true;
    public GameObject panelDisplay;

    void Create()
    {
        GetComponent<Renderer>().material.color = color2;
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
                Debug.Log("Something Hit");
                if (raycastHit.collider.gameObject.name == "Shutdown")
                {
                    Debug.Log("Clicked changing color");
                    GetComponent<Renderer>().material.color = change ? color1 : color2;
                    if (change)
                    {
                        panelDisplay.SetActive(true);
                    }
                    else
                    {
                        panelDisplay.SetActive(false);
                    }
                    change = !change;

                }
            }
        }
    }
}
